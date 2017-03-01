import numpy as np
from neupy import algorithms,plots
import matplotlib.pyplot as plt

dhnet = algorithms.DiscreteHopfieldNetwork(mode='async', check_limit=False) 

def ascii_visualise(bin_array, m=10,n=10):
	'''
	Basic visualisation for debug purposes: print binary array as m x n matrix
	'''
	
	for row in bin_array.reshape((n,m)).tolist():
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
	dhnet.train(np.concatenate(data, axis = 0))
	
def run(input, iterations=None, show=False):
	result = dhnet.predict(input, iterations)
	if show:
		ascii_visualise(result)
	return result

def show_weights():
	plt.figure(figsize=(14,12))
	plt.title("Hinton diagram (weights)")
	plots.hinton(dhnet.weight)
	plt.show()

	
if __name__ == "__main__":
	training_data = read_data("hopfield-numbers-10x10-training.txt")
	train(training_data)
	print(dhnet.weight)
	test_data = read_data("hopfield-numbers-10x10-test.txt")
	ascii_visualise(test_data[1])
	for i in range(1,100,5): 
	#This doesn't actually stabilise, as the entire iteration is reset for every run.
	#Unfortunatly, the method provided by Neupy doesn't give enough control over iteration or stopping early.
	#TODO: Create iterative method for neupy.algorithms.DiscreteHopfieldNetwork
		print()
		run(test_data[1],iterations=i,show=True)
	