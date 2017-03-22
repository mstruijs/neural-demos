import numpy as np
import random
import matplotlib.pyplot as plt
from neupy import layers, algorithms,plots, init
from neupy.layers.utils import iter_parameters




def reset_network(verbose=True):
	global adaline 
	#Define network topology.
	network = layers.Input(3, name='input') > layers.Linear(1,weight=init.Constant(0),bias=None,name='output')
	if verbose:
		#show network details
		for layer, attrname, parameter in iter_parameters(network):
			# parameter is shared Theano variable
			parameter_value = parameter.get_value()
			print("Layer: {}".format(layer))
			print("Parameter name: {}".format(attrname))
			print("Parameter shape: {}".format(parameter_value.shape))
			print()
	
	#construct network with learning rule to get adaline.
	#GradientDescent on a linear activation function means the delta rule is used.
	adaline = algorithms.GradientDescent(
		network,
		step=0.001,
		show_epoch=100,
		verbose=verbose
	)

reset_network()
	

def random_train_adaline(values, buy_limit, samples, plot=True, epochs=500,epsilon=0.025,test_data=None, iterative_sampling=False):
	'''
	Trains the adaline by providing randomly selected amounts of the values and determining the correct cost but adding them in the right values.

	values : the price of each product
	buy_limit : maximum amount bought of every product for a sample
	samples : the number of random samples generated
	plot : show the error plot. Might require the appropriate visualisation program.
	epochs : the number of iterations used to train the adaline. Will stop earlier if it has converged or convergence is impossible
	epsilon : Epsilon used for the delta rule 
	test_data: optional test-cases to track input error
	iterative_sampling: Adds the samples one by one to the network and show the converged values
	'''
	#pick random tuples of products
	random_input = [[random.randint(0,buy_limit) for y in range(0,len(values))] for x in range(0,samples)]
	#decide training method
	train_method = train_iterative if iterative_sampling else train_adaline
	train_method(values, random_input, plot, epochs, epsilon, test_data)
		
def fixed_train_adaline(values, samples, plot=True, epochs=500, epsilon=0.025, test_data=None, iterative_sampling=False):
	'''
	Trains the adaline with provided samples.

	values : the price of each product
	samples : the number of random samples generated
	plot : show the error plot. Might require the appropriate visualisation program.
	epochs : the number of iterations used to train the adaline. Will stop earlier if it has converged or convergence is impossible
	epsilon : Epsilon used for the delta rule 
	test_data: optional test-cases to track input error
	iterative_sampling: Adds the samples one by one to the network and show the converged values	
	'''
	train_method = train_iterative if iterative_sampling else train_adaline
	train_method(values, samples, plot, epochs, epsilon, test_data)
		
def train_adaline(values, samples, plot=True, epochs=500,epsilon=0.01, test_data=None, iterative_sampling=False):
	'''
	Trains the adaline by providing randomly selected amounts of the values and determining the correct cost but adding them in the right values.

	values : the price of each product
	samples : A list of samples, where each sample is a list that indicates how much of every product has been bought
	plot : show the error plot. Might require the appropriate visualisation program.
	epochs : the number of iterations used to train the adaline. Will stop earlier if it has converged or convergence is impossible
	epsilon : Epsilon used for the delta rule 	
	test_data: optional test-cases to track input error
	'''
	output = [sum([sample[i]*values[i] for i in range(0,len(values))]) for sample in samples]
	training_data = np.array(samples)
	output_data = np.array(output)
	if test_data != None:
		adaline.train(training_data, output_data, test_data[0], test_data[1], epochs=epochs,epsilon=epsilon)		
	else:
		adaline.train(training_data, output_data,epochs=epochs,epsilon=epsilon)
	if plot:
		plots.error_plot(adaline)

def train_iterative(values, samples, plot=True, epochs=500, epsilon=0.025, test_data=None):
	'''
	'''
	output = [sum([sample[i]*values[i] for i in range(0,len(values))]) for sample in samples]
	if test_data == None:
		print("Setting default test_data")
		test_data = [
			np.diag(np.ones(len(values),dtype=np.int)),
			np.array(values)
		]
	current_epoch = 0
	epochs_endpoints = [0]
	for i in range(1,len(samples)):
		adaline.train(np.array(samples[0:i]), np.array(output[0:i]), test_data[0], test_data[1], epochs=current_epoch+epochs,epsilon=epsilon)
		print("Iteration " + str(i) + ", adding sample" + str(samples[i-1]) + ":")
		results=[0,0,0]
		results[0] = adaline.predict([[1,0,0]])[0][0]
		results[1] = adaline.predict([[0,1,0]])[0][0]
		results[2] = adaline.predict([[0,0,1]])[0][0]
		print("Fish price: {:.2}".format(results[0]))
		print("Chips price: {:.2}".format(results[1]))
		print("Ketchup price: {:.2}".format(results[2]))
		print()
		current_epoch = adaline.last_epoch
		epochs_endpoints.append(current_epoch)
	if plot:
		ax = plt.gca().set_yscale('log')
		for epochs_endpoint in epochs_endpoints:
			plt.axvline(x=epochs_endpoint,linestyle='dotted',linewidth=0.2,color='black')
		plt.axhline(y=1/10,linestyle='dotted',color='green')
		plots.error_plot(adaline,ax=ax)
	
if __name__ == "__main__":
	
	random_train_adaline([5,3,1], 10, 10,plot=True,test_data=None, iterative_sampling=True)
	reset_network()
	a = np.array([1,2,3])
	b = np.array([1,3,3])
	c = np.array([1,4,2])
	fixed_data = [a, 2*a, b, 1.5*b, 3*a, 0.5*b, c, 2.5*a, 3*b, 2*c] #highly linearly dependendant dataset
	fixed_train_adaline([5,3,1],fixed_data,plot=True,test_data=None, iterative_sampling=True)
	