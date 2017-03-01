import numpy as np
from neupy import algorithms

dhnet = algorithms.DiscreteHopfieldNetwork(check_limit=False) 

def ascii_visualise(bin_matrix):
	'''
	Basic visualisation for debug purposes
	'''
	for row in bin_matrix.tolist():
		print(' '.join('.X'[val] for val in row))

def read_data(filename):
	'''
	Read the training/test data from file and return it in a list of matrices.
	'''
	res = [];
	m = [[]];
	rf = open(filename, 'r')
	for line in rf.readlines():
		if len(line) == 1:#empty line			
			res.append(np.matrix(m[:-1]))
			m = [[]];
			continue
		for char in line.strip():
			m[len(m)-1].append(1 if char=='X' else 0)
		m.append([])
	res.append(np.matrix(m[:-1]))
	rf.close()
	return res

def train(data):
	dhnet.train(np.concatenate(data, axis = 0))
	
def run(input, iterations=None, show=False):
	result = dhnet.predict(input, iterations)
	if show:
		ascii_visualise(result)
	return result
	
if __name__ == "__main__":
	training_data = read_data("hopfield-numbers-10x10-training.txt")
	train(training_data[:-1])
	test_data = read_data("hopfield-numbers-10x10-test.txt")
	ascii_visualise(test_data[0])
	for i in [62,144,232,379]:
		print()
		run(test_data[0],i,show=True)
		