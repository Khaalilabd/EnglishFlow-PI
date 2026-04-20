# WebRTC Signaling Service Dockerfile
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci --only=production

FROM node:18-alpine
RUN apk add --no-cache curl
RUN addgroup -S node && adduser -S node -G node
WORKDIR /app

# Copy dependencies and application
COPY --from=builder /app/node_modules ./node_modules
COPY server.js .
COPY package.json .

RUN chown -R node:node /app
USER node
EXPOSE 3001

HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD curl -f http://localhost:3001/health || exit 1

CMD ["node", "server.js"]
