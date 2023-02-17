import torch
import os
import skimage
import skimage.draw
import numpy as np
import matplotlib.pyplot as plt
import warnings
import pathlib
import torch
import numpy as np
import matplotlib.pyplot as plt


image_output_dir = pathlib.Path("image_processed")
image_output_dir.mkdir(exist_ok=True, parents=True)

def compute_loss_and_accuracy(dataloader, model, loss_function):
    """
    Computes the total loss and accuracy over the whole dataloader
    Args:
        dataloder: Test dataloader
        model: torch.nn.Module
        loss_function: The loss criterion, e.g: nn.CrossEntropyLoss()
    Returns:
        [loss_avg, accuracy]: both scalar.
    """
    model.eval()
    # Tracking variables
    loss_avg = 0
    total_correct = 0
    total_images = 0
    total_steps = 0
    with torch.no_grad():  # No need to compute gradient when testing
        for (X_batch, Y_batch) in dataloader:
            # Forward pass the images through our model
            X_batch, Y_batch = to_cuda([X_batch, Y_batch])
            output_probs = model(X_batch)
            # Compute loss
            loss = loss_function(output_probs, Y_batch)

            # Predicted class is the max index over the column dimension
            predictions = output_probs.argmax(dim=1).squeeze()
            Y_batch = Y_batch.squeeze()

            # Update tracking variables
            loss_avg += loss.cpu().item()
            total_steps += 1
            total_correct += (predictions == Y_batch).sum().cpu().item()
            total_images += predictions.shape[0]
    model.train()
    loss_avg = loss_avg / total_steps
    accuracy = total_correct / total_images
    return loss_avg, accuracy


def plot_loss(loss_dict, label):
    global_steps = list(loss_dict.keys())
    loss = list(loss_dict.values())
    plt.plot(global_steps, loss, label=label)


def to_cuda(elements):
    """
    Transfers all parameters/tensors to GPU memory (cuda) if there is a GPU available
    """
    if not torch.cuda.is_available():
        return elements
    if isinstance(elements, tuple) or isinstance(elements, list):
        return [x.cuda() for x in elements]
    return elements.cuda()


def to_uint8(im):
    # Converts and squashes an image to uint8.
    # If image min/max is outside of [0.0, 1.0]
    # We squash it to [0.0, 1.0]
    if im.dtype == np.uint8:
        return im
    if im.min() >= 0.0 and im.max() <= 1.0:
        im = (im*255).astype(np.uint8)
        return im
    warnings.warn("Image min/max is outside the range [0.0, 1.0]. Squashing the image to this range. (Can be safely ignored)")
    im = im - im.min()
    im = im / im.max()
    im = (im*255).astype(np.uint8)
    return im


def save_im(imname, im):
    im = to_uint8(im)
    impath = os.path.join(image_output_dir, imname)
    os.makedirs(image_output_dir, exist_ok=True)
    print("Saving image to:", impath)
    skimage.io.imsave(impath, im)


def uint8_to_float(im: np.array):
    """
    Converts an image from range 0-255 to 0-1
    Args:
    Args:
        im: np.array
    Returns:
        im: np.array with same shape as np.array

    """
    if im.dtype == np.float32:
        warnings.warn("Image is already np.float32")
        return im
    im = im.astype(np.float32) / 255
    return im


def create_high_pass_frequency_kernel(im: np.array,
                                      radius: int):
    """
    Creates a high pass filter with size radius.
    Returns a kernel in the frequency domain

    Args:
        im: np.array of shape [H, W]
    Returns:
        kernel: np.array of shape [H, W]
    """
    center_col = im.shape[1] // 2
    center_row = im.shape[0] // 2
    assert len(im.shape) == 2,\
        "Expected a grayscale image. Got image shape: {}".format(im.shape)
    kernel = np.ones_like((im))
    rr, cc = skimage.draw.disk((center_row, center_col), radius)
    kernel[rr, cc] = 0.0
    kernel = np.fft.fftshift(kernel)
    return kernel


def create_low_pass_frequency_kernel(im, radius):
    """
    Creates a low pass filter with size radius.
    Returns a kernel in the frequency domain

    Args:
        im: np.array of shape [H, W]
    Returns:
        kernel: np.array of shape [H, W]
    """
    kernel = create_high_pass_frequency_kernel(im, radius)
    kernel = 1 - kernel
    return kernel


def read_im(filepath):
    im = plt.imread(filepath)
    if im.dtype == np.uint8:
        im = im.astype(float) / 255
    return im


def normalize(im: np.ndarray) -> np.ndarray:
    """normalize image to the range [0, 1]

    Args:
        im ([np.ndarray]): [image of shape [H, W, C]]
    """
    im = im.astype(np.float32)
    return (im - im.min()) / (im.max() - im.min())


def find_angle(im_binary: np.ndarray) -> float:
    """Uses a hough line transform to find a straight line through points in the input image

    Args:
        im_binary ([np.ndarray, np.bool]): [A binary image of shape [H, W]]
        verbose ([bool]): [to plot or not]

    Returns:
        [float]: [The angle in degrees]
    """
    angles = np.linspace(-np.pi/2, np.pi/2, 360)
    h, theta, d = skimage.transform.hough_line(im_binary, theta=angles)
    _, angles, distances = skimage.transform.hough_line_peaks(
        h, theta, d, num_peaks=1)
    return angles, distances


def create_hough_line_image(im, angles, distances):
    hough_im = np.zeros_like(im)
    for angle, dist in zip(angles, distances):
        for x in range(0, im.shape[1]):
            y = int((dist - x * np.cos(angle)) / np.sin(angle))
            if y < 0 or y >= hough_im.shape[0]:
                continue
            hough_im[y, x] = 1
    return hough_im


def np_make_image_grid(images, nrow, pad=2):
    """Generates a image grid from a list om images

    Args:
        images ([list]): [A list of images with the same shape]
        nrow ([int]): [Number of rows to use when creating the image grid]
        pad (int, optional): [description]. Defaults to 2.

    Returns:
        [type]: [description]
    """
    height, width = images[0].shape[:2]
    ncol = int(np.ceil(len(images) / nrow))
    ncolors = images[0].shape[-1]
    result_imshape = [nrow * (height + pad), ncol * (width + pad), ncolors]
    if len(images[0].shape) == 2: # grayscale image
        ncolors = 1
        result_imshape[-1] = 1
    im_result = np.zeros(result_imshape, dtype=images[0].dtype)
    im_idx = 0
    for row in range(nrow):
        for col in range(ncol):
            if im_idx == len(images):
                break
            im = images[im_idx]
            im = normalize(im)
            im_idx += 1
            im_result[row * (pad + height): (row) * (pad + height) + height,
                      col * (pad + width): (col) * (pad + width) + width, :] = im.reshape(height, width, -1)
    if ncolors == 1:
        im_result = im_result[:, :, 0]
    return im_result