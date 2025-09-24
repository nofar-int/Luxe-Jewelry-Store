# Build stage
FROM node:18-alpine AS build

WORKDIR /app

# העתק את קבצי ה-NPM מהתיקייה jewelry-store
COPY jewelry-store/package.json jewelry-store/package-lock.json ./ 

# התקן את התלויות
RUN npm install

# העתק את שאר הקוד מהתיקייה jewelry-store
COPY jewelry-store/. .

# הרץ build אם יש צורך
RUN npm run build

# Production stage
FROM nginx:alpine

# העתק את התוצר מ-build לתוך nginx
COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]

