from test_helper import run_common_tests

if __name__ == '__main__':
    run_common_tests('''# Here is the comment for comments.py file
# type here''', '''# Here is the comment for comments.py file
#''', "You should type new comment")
