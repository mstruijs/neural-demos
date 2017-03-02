import numpy as np
from neupy import algorithms,plots
import matplotlib.pyplot as plt
from neupy.utils import format_data
from neupy.algorithms.memory.utils import bin2sign,step_function

dhnet = algorithms.DiscreteHopfieldNetwork(mode='async', check_limit=False)
iteration = 0
output_data = None
n_features = 0

def ascii_visualise(bin_vector, m=10,n=10):
	'''
	Basic visualisation for debug purposes: print binary vector as m x n matrix
	'''	
	for row in bin_vector.reshape((n,m)).tolist():
		print(' '.join('.X'[val] for val in row))

def read_data(filename):
	'''
	Read the training/test data from file and return it in a list of matrices.
	'''
	res = [];
	m = [];
	rf = open(filename, 'r')
	for line in rf.readlines():
		if len(line) == 1:#empty line	
			res.append(np.matrix(m))
			m = [];
			continue
		for char in line.strip():
			m.append(1 if char=='X' else 0)
	res.append(np.matrix(m))
	rf.close()
	return res

def train(data):
	'''
	Train the network with the supplied data
	'''
	dhnet.train(np.concatenate(data, axis = 0))
	
def run(input, iterations=None, show=False):
	'''
	Run the trained network with the given input, for the specified number of iterations. 
	Print the the result if `show`
	'''
	result = dhnet.predict(input, iterations)
	if show:
		ascii_visualise(result)
		print()
	return result

def show_weights():
	'''
	Plot the weight matrix in a Hinton diagram
	'''
	plt.figure(figsize=(14,12))
	plt.title("Hinton diagram (weights)")
	plots.hinton(dhnet.weight)
	plt.show()

def initialise_run(input_data):
	'''
	Prepare a controlled iteration on a trained network for the given input
	'''
	global iteration,dhnet,output_data,n_features
	iteration = 0
	dhnet.discrete_validation(input_data)
	input_data = format_data(bin2sign(input_data), is_feature1d=False)
	_, n_features = input_data.shape
	output_data = input_data
	
def step(step_size=1, show=False):
	'''
	Execute `step_size` asynchronous update steps on the initialised network.
	Print the result if `show`.
	'''
	global iteration,dhnet,output_data,n_features
	for _ in range(step_size):		
		iteration+=1
		position = np.random.randint(0, n_features - 1)
		raw_new_value = output_data.dot(dhnet.weight[:, position])
		output_data[:, position] = np.sign(raw_new_value)
	result = step_function(output_data).astype(int)
	if show:
		print("Iteration " + str(iteration) + ":")
		ascii_visualise(result)
	return result

def is_stable():
	'''
	Return True iff the initialised network has reached a stable output
	'''
	global dhnet,output_data,n_features,iteration
	for position in range(0,n_features-1):
		raw_new_value = output_data.dot(dhnet.weight[:, position])
		if np.sign(raw_new_value) != output_data[0][position]:
			return False
	return True

def run_to_convergence(input_data, show_list=[]):
	'''
	Runs a trained network on `input_data` until it converges to a stable output.
	Print the intermediate output at all positions in `show_list`.
	'''
	initialise_run(input_data)
	i=0
	result = None
	while not(is_stable()):
		i+=1
		result=step(show=(i in show_list))
	return result
	
if __name__ == "__main__":
	training_data = read_data("hopfield-numbers-10x10-training.txt")
	train(training_data)
	#print(dhnet.weight)
	test_data = read_data("hopfield-numbers-10x10-test.txt")
	ascii_visualise(test_data[0])
	step_run = False
	if step_run:
		initialise_run(test_data[0])
		for i in range(1,300,5):
		#This doesn't actually stabilise, as the entire iteration is reset for every run.
		#Unfortunatly, the method provided by Neupy doesn't give enough control over iteration or stopping early.
		#TODO: Create iterative method for neupy.algorithms.DiscreteHopfieldNetwork
			print("Iteration " + str(i) + ":")
			step(step_size=5,show=True)
			if is_stable():
				break
	else:
		res = run_to_convergence(test_data[0],[62,144,232,379])
		print("Iteration " + str(iteration) + ":")
		ascii_visualise(res)