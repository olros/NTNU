import torch
import os
import skimage
import skimage.io
import numpy as np
import matplotlib.pyplot as plt
import warnings
import pathlib

image_output_dir = pathlib.Path("image_processed")



def read_image(imname: str, image_folder=pathlib.Path("images")) -> np.ndarray:
    """
        Reads image (imname) from folder image_folder
    """
    impath = image_folder.joinpath(imname)
    print("Reading image:", impath)
    return skimage.io.imread(impath)


def to_uint8(im: np.ndarray) -> np.ndarray:
    """
        Converts and squashes an image to uint8.
        If image min/max is outside of [0.0, 1.0]
        We squash it to [0.0, 1.0]
        args:
            im: np.ndarray of dtype np.uint8 or np.float
        returns:
            im: np.ndarray of dtype np.uint8 in range [0, 255]
    """
    
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


def save_im(imname: str, im: np.ndarray):
    """
        Saves image (im) to the directory image_output_dir with 
        filename imname
    """
    im = to_uint8(im)
    impath = image_output_dir.joinpath(imname)
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
