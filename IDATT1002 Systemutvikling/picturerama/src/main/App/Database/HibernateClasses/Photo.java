package Database.HibernateClasses;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Hibernate database class for the table PHOTOS
 */
@Entity
@Table(name = "PHOTOS")
public class Photo implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", unique = true)
	private int id;
	@Column(name = "title")
	private String title;
	@Column(name = "url")
	private String url;
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "longitude")
	private Double longitude;
	@Column(name = "width")
	private Integer width;
	@Column(name = "height")
	private Integer height;
	@Column(name = "file_type")
	private String fileType;
	@Column(name = "file_size")
	private Integer fileSize;
	@Column(name = "aperture")
	private String aperture;
	@Column(name = "exposure_time")
	private String exposureTime;
	@Column(name = "camera_model")
	private String camera;
	@Column(name = "time")
	private String time;
	@Column(name = "user_id")
	private int userId;
	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "photo_id", nullable = false, insertable = false)
	private List<Tags> tags = new ArrayList<>();
	@ManyToMany(mappedBy = "photos", cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
	private Set<Album> albums = new HashSet<>();

	public Photo() {
	}

	public Photo(String title, String url, Double latitude, Double longitude, Integer width, Integer height, String fileType, Integer fileSize, String aperture, String exposureTime, String camera, String time, int userId) {
		this.title = title;
		this.url = url;
		this.latitude = latitude;
		this.longitude = longitude;
		this.width = width;
		this.height = height;
		this.fileType = fileType;
		this.fileSize = fileSize;
		this.aperture = aperture;
		this.exposureTime = exposureTime;
		this.camera = camera;
		this.time = time;
		this.userId = userId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public Integer getFileSize() {
		return fileSize;
	}

	public void setFileSize(Integer fileSize) {
		this.fileSize = fileSize;
	}

	public String getAperture() {
		return aperture;
	}

	public void setAperture(String aperture) {
		this.aperture = aperture;
	}

	public String getExposureTime() {
		return exposureTime;
	}

	public void setExposureTime(String exposureTime) {
		this.exposureTime = exposureTime;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getUserId() {
		return userId;
	}

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public List<Tags> getTags() {
		return tags;
	}

	public void setTags(List<Tags> tags) {
		this.tags = tags;
	}

	public Set<Album> getAlbums() {
		return albums;
	}

	public void addAlbum(Album album) {
		albums.add(album);
		album.getPhotos().add(this);
	}

	/**
	 * Convert the file size from bytes to kilo bytes
	 *
	 * @return the file size converted to kilo bytes, null if there is no fileSize registered
	 */
	private Double fileSizeAsKiloBytes() {
		if (fileSize != null) {
			return Math.round(100 * ((double) fileSize / 1024)) / 100.00;
		}
		return null;
	}

	@Override
	public String toString() {
		return "File type: " + getFileType() + "\n" +
				"Dimensions: " + getWidth() + " x " + getHeight() + "\n" +
				"Size: " + fileSizeAsKiloBytes() + " kB\n" +
				"Latitude: " + getLatitude() + "\n" +
				"Longitude: " + getLongitude() + "\n" +
				"Date: " + getTime() + "\n" +
				"Model: " + getCamera() + "\n" +
				"Aperture: " + getAperture() + "\n" +
				"Exposure time: " + getExposureTime() + "\n";
	}
}
