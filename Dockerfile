# Sử dụng base image với Tomcat 11 và JDK 21 (LTS mới nhất, tương thích với Tomcat 11)
FROM tomcat:11.0-jdk21

# Xóa các ứng dụng mặc định của Tomcat
RUN rm -rf /usr/local/tomcat/webapps/*

# Copy file WAR vào thư mục webapps của Tomcat (đổi tên thành ROOT.war để map vào context path "/")
COPY target/AlumniNetwork-1.0-SNAPSHOT.war /usr/local/tomcat/webapps/ROOT.war

# Cấp quyền truy cập (nếu cần)
RUN chmod -R 755 /usr/local/tomcat/webapps

# Expose cổng 8080 (Railway tự động map cổng này)
EXPOSE 8080

# Khởi chạy Tomcat
CMD ["catalina.sh", "run"]