FROM node:22-alpine

WORKDIR /app

COPY package.json ./
COPY tsconfig.json ./
COPY next.config.ts ./
COPY postcss.config.js ./
COPY tailwind.config.ts ./
COPY .eslintrc.json ./
COPY next-env.d.ts ./
COPY .env.local ./
COPY src ./src

RUN npm install

EXPOSE 3000

CMD ["npm", "run", "dev"]
