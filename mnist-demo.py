'''
An example of a convolutional neural network to construct a classifier for the mnist dataset.
Based on the example in http://neupy.com/2016/11/12/mnist_classification.html
Convolutional networks of this type have been applied quite succesfully for image classification challenges by Meier et al. ICDAR 2011 
see: http://people.idsia.ch/~ciresan/data/SCRpresentation.pdf and https://arxiv.org/pdf/1102.0183

Requires neupy, mnist.
NOTE: training the network is computationally intensive, 
so make sure Theano is configured correctly and has the dependencies such that compilation of linear algebra operations works as intended (GPU acceleration is not required). 
If not, Theano will note that certain c-libraries cannot be used, computations will be incredibly slow and will probably run out of memory, most likely creating a segmentation fault in the process.

When Theano is configured correctly, training should take only a few minutes. A single training epoch takes about 3 seconds on my machine (we use 20 epochs in this example).
I think the `Anaconda' python distribution (https://www.continuum.io/downloads) and installing the required numpy+mkl and scipy wheels via pip will give the required dependencies.
'''
import mnist
from neupy import algorithms, layers, plots
import numpy as np
import theano

theano.config.floatX = 'float32' #use smaller floats for efficiency

#Construct network
network = algorithms.Momentum(
	[
		layers.Input(784),
		layers.Relu(500),
		layers.Relu(300),
		layers.Softmax(10)
	],
	error='categorical_crossentropy',
	step=0.01,
	show_epoch=2,
	verbose=True,
	shuffle_data=True,
	momentum=0.99,
	nesterov=True,
)


def normalise(image):
	mean = image.sum()/len(image)
	return image - mean

def one_hot_encode(labels):
	res = np.zeros((len(labels),10),'float32')
	for i in range(0,len(labels)):
		res[i][labels[i]] = 1
	return res

def train(error_plot=False, test=False):
	#load data in matrices
	train_images=mnist.train_images().reshape((-1,784))
	train_labels=mnist.train_labels()
	test_images =mnist.test_images().reshape((-1,784))
	test_labels =mnist.test_labels()
	#Scale the images
	scaled_train_images = train_images/255
	scaled_test_images = test_images/255
	#normalise relative cell difference between images
	normalised_train_images = scaled_train_images - scaled_train_images.mean(axis=0)
	normalised_test_images = scaled_test_images - scaled_test_images.mean(axis=0)

	training_data = normalised_train_images.astype(np.float32)
	test_data = normalised_test_images.astype(np.float32)
	network.train(training_data, one_hot_encode(train_labels), test_data, one_hot_encode(test_labels), epochs = 20, summary='table')
	#Test the network on the test-dataset
	if test:
		predicted_labels = network.predict(test_data).argmax(axis=1)
		recorded_labels = np.array(test_labels)
		accuracy_score = sum([(1 if predicted_labels[i]==recorded_labels[i] else 0) for i in range(0,len(test_labels))])/len(test_labels)
		print("Validation accuracy: {:.2%}".format(accuracy_score)) #-> ~98.3
	#Plot the error rate
	if error_plot: 
		plots.error_plot(network)

		
if __name__ == "__main__" :
	train(error_plot=True, test=True)