FROM java:latest
MAINTAINER docker_user
RUN echo "Asia/shanghai" > /etc/timezone;
ADD /opt/newenergy/VehicleNewEnergyDataService /root/
WORKDIR /tmp
CMD ["nohup","java","-jar","/root/VehicleNewEnergyDataService.jar","&"]
