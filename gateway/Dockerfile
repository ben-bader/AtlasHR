# =========================
# BUILD STAGE
# =========================
FROM node:20-alpine AS builder

WORKDIR /app

COPY package*.json ./
RUN npm ci

COPY . .

# optional: if you later add TS
# RUN npm run build


# =========================
# RUNTIME STAGE
# =========================
FROM node:20-alpine

WORKDIR /app

ENV NODE_ENV=production

COPY package*.json ./
RUN npm ci --omit=dev

COPY --from=builder /app/src ./src

EXPOSE 3000

# improved healthcheck (real HTTP check)
HEALTHCHECK --interval=20s --timeout=5s --retries=5 \
  CMD node -e "http=require('http'); \
  http.get('http://localhost:3000/health', r=>process.exit(r.statusCode===200?0:1)) \
  .on('error',()=>process.exit(1));"

CMD ["node", "src/index.js"]