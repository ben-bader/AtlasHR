# =========================
# BUILD STAGE
# =========================
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./

# deterministic install (IMPORTANT FIX)
RUN npm ci

COPY . .

ARG NEXT_PUBLIC_API_URL
ARG NEXT_PUBLIC_APP_NAME

ENV NEXT_PUBLIC_API_URL=$NEXT_PUBLIC_API_URL
ENV NEXT_PUBLIC_APP_NAME=$NEXT_PUBLIC_APP_NAME

RUN npm run build


# =========================
# RUNTIME STAGE
# =========================
FROM node:20-alpine

WORKDIR /app

ENV NODE_ENV=production

# install only production deps
COPY package*.json ./
RUN npm ci --omit=dev

# copy standalone build (BEST PRACTICE)
COPY --from=builder /app/.next/standalone ./
COPY --from=builder /app/.next/static ./.next/static
COPY --from=builder /app/public ./public

EXPOSE 3000

# healthcheck using node (no curl/wget dependency)
HEALTHCHECK --interval=30s --timeout=10s --retries=3 \
  CMD node -e "require('http').get('http://localhost:3000', r => process.exit(r.statusCode === 200 ? 0 : 1))"

CMD ["node", "server.js"]