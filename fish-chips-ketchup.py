import numpy as np
import random
from neupy import layers, algorithms,plots, init
from neupy.layers.utils import iter_parameters

#Define network.
network = layers.Input(3) > layers.Linear(1,weight=init.Constant(0),bias=None)

#Toggle some debug printing
verbose = True 

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

def random_train_adaline(values, buy_limit, samples, plot=True, epochs=3000,epsilon=0.001):
	'''
	Trains the adaline by providing randomly selected amounts of the values and determining the correct cost but adding them in the right values.

	values : the price of each product
	buy_limit : maximum amount bought of every product for a sample
	samples : the number of random samples generated
	plot : show the error plot. Might require the appropriate visualisation program.
	epochs : the number of iterations used to train the adaline. Will stop earlier if it has converged or convergence is impossible
	epsilon : Epsilon used for the delta rule 
	'''
	#pick random tuples of 
	random_input = [[random.randint(0,buy_limit) for y in range(0,len(values))] for x in range(0,samples)]
	#compute the total cost of every tuple
	output = [sum([sample[i]*values[i] for i in range(0,len(values))]) for sample in random_input]
	print(random_input)
	print(output)
	training_data = np.array(random_input)
	output_data = np.array(output)	
	adaline.train(training_data, output_data,epochs=epochs,epsilon=epsilon)
	if plot:
		plots.error_plot(adaline)

		
if __name__ == "__main__":		
	
	random_train_adaline([5,3,1], 10, 10)
	
	print(adaline.predict([[1,0,0]]))
	print(adaline.predict([[0,1,0]]))
	print(adaline.predict([[0,0,1]]))