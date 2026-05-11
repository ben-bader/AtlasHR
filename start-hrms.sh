  #!/usr/bin/env bash
  # =============================================================================
  # HRMS -- Full Stack Startup Script
  # Usage: ./start-hrms.sh [--build] [--clean] [--logs] [--down]
  #
  #   --build   Force rebuild all images before starting
  #   --clean   Wipe all volumes + containers before starting (fresh slate)
  #   --logs    Tail logs of all services after startup
  #   --down    Tear everything down instead of starting
  # =============================================================================

  set -euo pipefail

  # -- Colors -------------------------------------------------------------------
  RED='\033[0;31m'
  GREEN='\033[0;32m'
  YELLOW='\033[1;33m'
  BLUE='\033[0;34m'
  CYAN='\033[0;36m'
  BOLD='\033[1m'
  NC='\033[0m'

  # -- Flags --------------------------------------------------------------------
  BUILD=false
  CLEAN=false
  LOGS=false
  DOWN=false

  for arg in "$@"; do
    case $arg in
      --build) BUILD=true ;;
      --clean) CLEAN=true ;;
      --logs)  LOGS=true  ;;
      --down)  DOWN=true  ;;
      *)
        echo -e "${RED}Unknown argument: $arg${NC}"
        echo "Usage: ./start-hrms.sh [--build] [--clean] [--logs] [--down]"
        exit 1
        ;;
    esac
  done

  # -- Paths (relative to this script) -----------------------------------------
  SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
  INFRA_COMPOSE="$SCRIPT_DIR/infrastructure/docker/docker-compose.yml"

  declare -A SERVICES
  SERVICES=(
    [auth-service]="$SCRIPT_DIR/apps/auth-service/docker-compose.yml"
    [employee-service]="$SCRIPT_DIR/apps/employee-service/docker-compose.yml"
    [api-gateway]="$SCRIPT_DIR/apps/gateway-service/docker-compose.yml"
    [frontend]="$SCRIPT_DIR/apps/frontend/docker-compose.yml"
  )

  # Start order matters -- auth before gateway, gateway before frontend
  SERVICE_ORDER=(
    auth-service
    employee-service
    api-gateway
    frontend
  )

  # -- Helpers ------------------------------------------------------------------
  log()     { echo -e "${BOLD}${BLUE}[HRMS]${NC} $*"; }
  success() { echo -e "${GREEN}  OK  $*${NC}"; }
  warn()    { echo -e "${YELLOW}  WARN  $*${NC}"; }
  error()   { echo -e "${RED}  ERR  $*${NC}"; }
  step()    { echo -e "\n${CYAN}---  $*  ---${NC}"; }

  wait_healthy() {
    local container="$1"
    local timeout="${2:-90}"
    local interval=3
    local elapsed=0

    echo -n "  Waiting for ${container} to be healthy..."

    until [ "$(docker inspect --format='{{.State.Health.Status}}' "$container" 2>/dev/null)" = "healthy" ]; do
      if [ "$elapsed" -ge "$timeout" ]; then
        echo ""
        error "${container} did not become healthy within ${timeout}s"
        docker logs "$container" --tail 20
        exit 1
      fi
      echo -n "."
      sleep "$interval"
      elapsed=$((elapsed + interval))
    done

    echo -e " ${GREEN}healthy${NC}"
  }

  compose_up() {
    local file="$1"
    local extra_flags=""
    $BUILD && extra_flags="--build"
    docker compose -f "$file" up -d $extra_flags
  }

  compose_down() {
    local file="$1"
    local extra_flags="--remove-orphans"
    $CLEAN && extra_flags="$extra_flags -v"
    docker compose -f "$file" down $extra_flags 2>/dev/null || true
  }

  check_file_exists() {
    local name="$1"
    local file="$2"
    if [ ! -f "$file" ]; then
      warn "Skipping ${name} -- compose file not found at: ${file}"
      return 1
    fi
    return 0
  }

  # -- Banner -------------------------------------------------------------------
  echo -e "
  ${BOLD}${BLUE}
    _   _ ____  __  __ ____
  | | | |  _ \|  \/  / ___|
  | |_| | |_) | |\/| \___ \\
  |  _  |  _ <| |  | |___) |
  |_| |_|_| \_\_|  |_|____/
  ${NC}${CYAN}  Human Resource Management System${NC}
  "

  # -- Tear Down Mode -----------------------------------------------------------
  if $DOWN; then
    step "Tearing down all HRMS services"

    for (( i=${#SERVICE_ORDER[@]}-1; i>=0; i-- )); do
      name="${SERVICE_ORDER[$i]}"
      file="${SERVICES[$name]}"
      if [ -f "$file" ]; then
        log "Stopping ${name}..."
        compose_down "$file"
        success "${name} stopped"
      fi
    done

    log "Stopping infrastructure..."
    compose_down "$INFRA_COMPOSE"
    success "Infrastructure stopped"

    if $CLEAN; then
      success "All volumes wiped -- clean slate ready"
    else
      success "All containers stopped (volumes preserved)"
    fi
    exit 0
  fi

  # -- Pre-flight checks --------------------------------------------------------
  step "Pre-flight checks"

  if ! command -v docker &>/dev/null; then
    error "Docker is not installed or not in PATH"
    exit 1
  fi
  success "Docker found: $(docker --version)"

  if ! docker compose version &>/dev/null; then
    error "Docker Compose v2 is not available"
    exit 1
  fi
  success "Docker Compose found: $(docker compose version --short)"

  if ! docker info &>/dev/null; then
    error "Docker daemon is not running. Start Docker Desktop or: sudo systemctl start docker"
    exit 1
  fi
  success "Docker daemon is running"

  # -- Optional clean wipe ------------------------------------------------------
  if $CLEAN; then
    step "Clean wipe -- removing all existing containers and volumes"
    warn "This will DELETE all database data. Press Ctrl+C within 5 seconds to abort..."
    sleep 5

    for (( i=${#SERVICE_ORDER[@]}-1; i>=0; i-- )); do
      name="${SERVICE_ORDER[$i]}"
      file="${SERVICES[$name]}"
      [ -f "$file" ] && compose_down "$file"
    done
    compose_down "$INFRA_COMPOSE"
    success "Clean wipe complete"
  fi

  # -- Step 1: Infrastructure ---------------------------------------------------
  step "Starting Infrastructure (Postgres, RabbitMQ, Redis)"

  if [ ! -f "$INFRA_COMPOSE" ]; then
    error "Infrastructure compose file not found at: $INFRA_COMPOSE"
    exit 1
  fi

  compose_up "$INFRA_COMPOSE"

  wait_healthy "hrms-postgres"  90
  wait_healthy "hrms-rabbitmq"  60
  wait_healthy "hrms-redis"     30

  success "Infrastructure is up and healthy"

  # -- Step 2: Application Services ---------------------------------------------
  step "Starting Application Services"

  STARTED=()
  SKIPPED=()

  for name in "${SERVICE_ORDER[@]}"; do
    file="${SERVICES[$name]}"

    if ! check_file_exists "$name" "$file"; then
      SKIPPED+=("$name")
      continue
    fi

    log "Starting ${name}..."
    compose_up "$file"

    container_name="hrms-${name}"

    # Check if container has a healthcheck before waiting
    has_healthcheck=$(docker inspect "$container_name" --format='{{if .Config.Healthcheck}}yes{{end}}' 2>/dev/null || echo "")

    if [ "$has_healthcheck" = "yes" ]; then
      wait_healthy "$container_name" 120
    else
      warn "${name} has no healthcheck -- waiting 10s before continuing..."
      sleep 10
    fi

    success "${name} is up"
    STARTED+=("$name")
  done

  # -- Summary ------------------------------------------------------------------
  step "Startup Complete"

  echo -e "\n${BOLD}Services started (${#STARTED[@]}):${NC}"
  for name in "${STARTED[@]}"; do
    echo -e "  ${GREEN}[OK]${NC}  ${name}"
  done

  if [ ${#SKIPPED[@]} -gt 0 ]; then
    echo -e "\n${BOLD}Services skipped -- compose file not found (${#SKIPPED[@]}):${NC}"
    for name in "${SKIPPED[@]}"; do
      echo -e "  ${YELLOW}[SKIP]${NC}  ${name}"
    done
  fi

  echo -e "\n${BOLD}Access points:${NC}"
  echo -e "  Frontend         ->  http://localhost:3000"
  echo -e "  API Gateway      ->  http://localhost:8080/api"
  echo -e "  Auth Service     ->  http://localhost:8081/actuator/health"
  echo -e "  Employee Service ->  http://localhost:8083/actuator/health"
  echo -e "  RabbitMQ UI      ->  http://localhost:15672  (hrms / hrms_pass)"
  echo -e "  Postgres         ->  localhost:5432  (hrms / hrms_pass)"
  echo -e "  Redis            ->  localhost:6379"

  echo -e "\n${BOLD}Useful commands:${NC}"
  echo -e "  ./start-hrms.sh --down   Stop everything"
  echo -e "  ./start-hrms.sh --clean  Wipe and restart fresh"
  echo -e "  ./start-hrms.sh --build  Rebuild images and start"
  echo -e "  ./start-hrms.sh --logs   Tail all logs after start"

  # -- Optional log tailing -----------------------------------------------------
  if $LOGS; then
    step "Tailing logs (Ctrl+C to stop)"
    compose_args=("-f" "$INFRA_COMPOSE")
    for name in "${SERVICE_ORDER[@]}"; do
      file="${SERVICES[$name]}"
      [ -f "$file" ] && compose_args+=("-f" "$file")
    done
    docker compose "${compose_args[@]}" logs -f --tail=50
  fi