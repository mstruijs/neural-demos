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

Additionally, installing GraphViz is required to show plots.
'''
import mnist
import subprocess
import sys
from neupy import algorithms, layers, plots, storage
import numpy as np
import theano
from scipy import misc

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

##
#  Prepare and normalise matrices
##

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


def normalise(image):
	mean = image.sum()/len(image)
	return image - mean

def one_hot_encode(labels):
	res = np.zeros((len(labels),10),'float32')
	for i in range(0,len(labels)):
		res[i][labels[i]] = 1
	return res

def train(error_plot=False, test=False, storage_name=None):
	'''
	Trains the current network with the MNIST training dataset and 
	optionally also plots the errors, validates using the testset, or stores the network in a .pickle file.
	'''
	training_data = normalised_train_images.astype(np.float32)
	test_data = normalised_test_images.astype(np.float32)
	network.train(training_data, one_hot_encode(train_labels), test_data, one_hot_encode(test_labels), epochs = 20, summary='table')
	if test:
		test_network(error_plot)
	#Store the network, if specified
	if storage_name!=None:
		storage.save(network, filepath = "data/" + storage_name + ".pickle")

		
def classify(index, network_storage=None, test=False,show=True):
	'''
	Uses the current network to attempt to classify an image of the test set at the given index. 
	Optionally, network_storage is loaded first, the network is tested first or the image can not be shown.
	'''
	image = normalised_test_images[index]
	image_class = test_labels[index]
	if network_storage!=None:
		storage.load(network, "data/" + network_storage + ".pickle")
	if test:
		test_network()
	if show:
		images= mnist.test_images()
		show_image(images[index,:,:], 'MNISTtemp')
	print("Classifying image #"+ str(index) + " of class '"+ str(image_class) + "'...")
	predicted_class = network.predict(image).argmax(axis=1)[0]
	if predicted_class==image_class:
		print("The network correctly classified the image as '" + str(predicted_class) +"'.")
	else:
		print("The network incorrectly classified the image as '" + str(predicted_class) +"', while the actual class is '" + str(image_class) + "'.")
		

def test_network(error_plot=False):
	#Test the network on the test-dataset
	test_data = normalised_test_images.astype(np.float32)
	predicted_labels = network.predict(test_data).argmax(axis=1)
	recorded_labels = np.array(test_labels)
	accuracy_score = sum([(1 if predicted_labels[i]==recorded_labels[i] else 0) for i in range(0,len(test_labels))])/len(test_labels)
	print("Validation accuracy: {:.2%}".format(accuracy_score)) #-> ~98.3
	#Plot the error rate
	if error_plot: 
		plots.error_plot(network)

def show_image(image, filename):
	'''
	Takes an image matrix, saves it to a filename and attempts to immediately display it.
	'''
	misc.toimage(misc.imresize(image * -1 + 256, 10.)).save('data/' + filename + ".jpg")
	if sys.platform in ["win32","cygwin"]:#Calling explorer only works on an os that has an 'explorer'.
		subprocess.run("explorer \".\data\\" + filename + ".jpg\"")	
	else:
		print("I don't know what standard image viewer there is on this os: {} ".format(sys.platform))
		
if __name__ == "__main__" :
	#images = mnist.train_images()
	#show_image(images[0,:,:], "aha")
	#train(error_plot=True, test=True, storage_name="MNISTdemo1")
	#classify(0,network_storage="MNISTdemo1",test=True)