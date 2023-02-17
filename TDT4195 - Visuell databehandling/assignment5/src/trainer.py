
import torch
import tqdm
import utils
import collections

torch.random.manual_seed(0)


class Trainer:

    def __init__(self,
                 model,
                 dataloader_train,
                 dataloader_test,
                 batch_size,
                 loss_function,
                 optimizer):
        self.dataloader_train = dataloader_train
        self.dataloader_test = dataloader_test
        self.batch_size = batch_size

        self.model = model
        self.loss_function = loss_function
        self.optimizer = optimizer

    def train(self, num_epochs):
        tracked_train_loss = collections.OrderedDict()
        tracked_test_loss = collections.OrderedDict()
        global_step = 0
        for epoch in range(num_epochs):
            avg_loss = 0
            for batch_it, (images, target) in enumerate(
                    tqdm.tqdm(self.dataloader_train,
                              desc=f"Training epoch {epoch}")):
                # images has shape: [batch_size, 1, 28, 28]
                # target has shape: [batch_size]
                # Transfer batch to GPU VRAM if a GPU is available.
                images, target = utils.to_cuda([images, target])
                # Perform forward pass
                logits = self.model(images)

                # Compute loss
                loss = self.loss_function(logits, target)

                avg_loss += loss.cpu().detach().item()
                # Perform backpropagation
                loss.backward()

                # Update our parameters with gradient descent
                self.optimizer.step()

                # Reset our model parameter gradients to 0
                self.optimizer.zero_grad()

                # Track the average loss for every 500th image
                if batch_it % (500//self.batch_size) == 0 and batch_it != 0:
                    avg_loss /= (500//self.batch_size)
                    tracked_train_loss[global_step] = avg_loss
                    avg_loss = 0
                global_step += self.batch_size
            # Compute loss and accuracy on the test set
            test_loss, test_acc = utils.compute_loss_and_accuracy(
                self.dataloader_test, self.model, self.loss_function
            )
            tracked_test_loss[global_step] = test_loss
        return tracked_train_loss, tracked_test_loss

    def save_model(self, savepath):
        torch.save(savepath, self.model.state_dict())

    def load_model(self, model_path):
        state_dict = torch.load(model_path)
        self.model.load_state_dict(state_dict)