# Picturerama

Image database project in Systemutvikling 1 at NTNU

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes.

### Prerequisites
- Java 12 SDK
- IntelliJ IDEA or another code editor

### Installing
```
git clone git@gitlab.stud.idi.ntnu.no:gruppe-12/picturerama.git
```

In order to be able to connect to our database, be able to upload local images and use the map, you'll need a ```config.properties``` file in the project root. It should look like this:
```
username=your_username
password=your_password
database_url=jdbc:mysql://your_database_url
cloudinary_cloud_name=your_cloud_name
cloudinary_api_key=your_api_key
cloudinary_api_secret=your_api_secret
google_maps_api_key=your_google_maps_api_key
```

Contact one of the developers to get our config.properties file.

If you want to setup your own database for the application. Run the SetupDatabase file in the Main folder in the project, and use your own info in the config.properties file.

You should now have the files you need. In order to be able to run, you have to compile JavaFX

**Steps to compile JavaFX in IntelliJ IDEA:**
1. MAVEN
2. Picturerama
3. Plugins
4. Double click on javafx:compile
5. Build project (hammer)
6. Right click javafx:run and click "Run 'Picturerama'"