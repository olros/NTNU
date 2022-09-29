from functools import reduce

k_0 = -100  # Negativ investering
k = [40, 50, 60] # k_0 = første element i listen
r = 0.08
N_e = len(k)

print(f"Bruker k_0={k_0}, k={k}, r={r}, N_e={N_e}")

def nnv_step(a, b):
	return a + (b[1] / ((1 + r) ** (b[0] + 1)))

NNV = reduce(nnv_step, enumerate(k), k_0)

print("NNV: ", NNV)

annu = NNV * (r / (1 - (1 + r) ** -N_e))

print("annu: ", annu)
