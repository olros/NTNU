black:
	black . ${args} --exclude migrations

isort:
	isort . ${args}

flake8:
	flake8 .

format:
	make black
	make isort

check:
	make black args="--check"
	make isort args="--check-only"
	make flake8