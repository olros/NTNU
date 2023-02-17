import torch
import numpy as np
import matplotlib.pyplot as plt


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
            total_correct += (predictions == Y_batch).sum().item()
            total_images += predictions.shape[0]
    model.train()
    loss_avg = loss_avg / total_steps
    accuracy = total_correct / total_images
    return loss_avg, accuracy


def plot_loss(loss_dict, label):
    global_steps = list(loss_dict.keys())
    loss = list(loss_dict.values())
    plt.plot(global_steps, loss, label=label)


def read_im(filepath):
    im = plt.imread(filepath)
    if im.dtype == np.uint8:
        im = im.astype(float) / 255
    return im


def normalize(im):
    return (im - im.min()) / (im.max() - im.min())


def save_im(filepath, im, cmap=None):
    if im.min() < 0 or im.max() > 1:
        print(
            "Warning: The dynamic range of the image is",
            f"[{im.min()}, {im.max()}]",
            "normalizing to [-1, 1]")
        im = normalize(im)
    plt.imsave(filepath, im, cmap=cmap)


def to_cuda(elements):
    """
    Transfers all parameters/tensors to GPU memory (cuda) if there is a GPU available
    """
    if not torch.cuda.is_available():
        return elements
    if isinstance(elements, tuple) or isinstance(elements, list):
        return [x.cuda() for x in elements]
    return elements.cuda()
