package Components;

import Database.HibernateClasses.Photo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Class that is used to analyze a image and extract all its metadata
 */
public final class ImageAnalyzer {

	/**
	 * Private constructor to hinder creation of utility class
	 */
	private ImageAnalyzer() {
		throw new IllegalStateException("Can not make instance of utility class");
	}

	/**
	 * Analyze an image located at given url
	 *
	 * @param title Is the title of the image that is getting analyzed
	 * @param url   Is the url to where the image is saved
	 * @return A Photo object that contains all the image metadata of the image
	 * @throws IOException          Is thrown when there is something wrong with the url
	 * @throws NullPointerException Is thrown when if something is set ass null in the metadata, that cant be null
	 */
	public static Photo analyze(String title, String url) throws IOException {
		URL path = new URL(url);
		URLConnection conn = path.openConnection();
		InputStream in = conn.getInputStream();
		javaxt.io.Image image = new javaxt.io.Image(in);
		java.util.HashMap<Integer, Object> exif = image.getExifTags();

		Photo photo = new Photo();
		double[] coord = image.getGPSCoordinate();
		photo.setTitle(title);
		photo.setUrl(url);
		photo.setTime((String) exif.get(0x0132));
		photo.setCamera((String) exif.get(0x0110));
		photo.setExposureTime((String) exif.get(0x829A));
		photo.setAperture((String) exif.get(0x9202));
		if (coord != null) {
			photo.setLongitude(coord[0]);
			photo.setLatitude(coord[1]);
		}
		photo.setHeight(image.getHeight());
		photo.setWidth(image.getWidth());
		photo.setFileSize(conn.getContentLength());
		photo.setFileType(conn.getContentType());
		photo.setUserId(UserInfo.getUser().getId());
		return photo;
	}
}
