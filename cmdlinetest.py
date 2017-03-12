import argparse

parser = argparse.ArgumentParser(description='Hopfield neural network')
parser.add_argument('-g','--train', help='Training data set path', required=True)
parser.add_argument('-t','--test', help='Testing data set path', required=True)
args = vars(parser.parse_args())
print args['train']
print args['test']