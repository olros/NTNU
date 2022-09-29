from functools import reduce


def nnv(project):
	print(f"Bruker G={project.G}, k={project.k}, r={project.r}, N_e={project.N_e}")
	return reduce(lambda a, b: a + (b[1] / ((1 + project.r) ** (b[0] + 1))), enumerate(project.k), -project.G)


#Konstant annbetalingsoverskudd a - ℎ𝑣𝑖𝑠 𝑎𝑡 𝑒𝑟 𝑘𝑜𝑛𝑠𝑡𝑎𝑛𝑡 𝑜𝑔 𝑙𝑖𝑘 𝑚𝑒𝑑 𝑎
def nnv_constant_a(project):
	return -project.G + project.a * (1 - (1 + project.r) ** (-project.N_e)) / project.r + project.S / ((1 + project.r) ** project.N_e)

class Project:
	"""
	@param G: investeringsutgift (positivt)
	@param a: årlig innbetalingsoverskudd
	@param S: restverdi
	@param N_e: økonomisk levetid
	@param r: rente som desimaltall
	@param k: liste med betalinger
	"""
	def __init__(self, name, G=0, a=0, S=None, N_e=0, r=0, k=None):
		self.name = name
		self.G = G
		self.a = a 
		self.S = S 
		self.N_e = N_e 
		self.r = r
		self.k = k

		if not S and not a and k:
			self.nnv_solver = nnv
		else: 
			self.nnv_solver = nnv_constant_a

	def nnv(self):
		return self.nnv_solver(self)

	def annu(self):
		return self.nnv() * (self.r / (1 - (1 + self.r) ** -self.N_e))

	def annu_by_r(self):
		return self.annu() / self.r

	def nnv_by_G(self):
		return self.nnv() / self.G

	def __str__(self) -> str:
			return "Prosjekt: " + self.name

	
projects = [
	Project("A", G=1000, k=[320,320,320,520], N_e=4, r=0.12),
	Project("B", G=1000, k=[280,280,280,280,480], N_e=5, r=0.12),
	Project("C", G=1000, k=[250,250,320,320,480], N_e=5, r=0.12),
	Project("D", G=1000, k=[260,260,260,260,400], N_e=6, r=0.12)
]

# project = Project(G=100, N_e=3, r=0.08, k=[40, 50, 60])

for project in projects:
	print(project)
	print("NNV:    ", project.nnv())
	print("annu:   ", project.annu())
	print("annu/r: ", project.annu_by_r())
	print("NNV/G:  ", project.nnv_by_G())
	print()


def __find_highest(method_name):
	highest = projects[0]
	for project in projects[1:]:
		if getattr(project, method_name)() > getattr(highest, method_name)():
			highest = project

	return highest	


def invest_only_once():
	"""Investeringene kan bare gjøres én gang. Finn prosjektet med høyest NNV"""
	return __find_highest("nnv")

def investment_repeats_indefinetly():
	"""Investeringene kan gjentas et uendelig antall ganger. Finn det med høyest annu/r"""
	return __find_highest("annu_by_r")
	
def investment_only_once_with_limited_capital():
	"""Investeringene bare kan gjøres én gang og det er mangel på kapital. Finn det med høyest NNV/G"""
	return __find_highest("nnv_by_G")
	

print("Investeringene kan bare gjøres én gang: ", invest_only_once())
print("Investeringene kan gjentas et uendelig antall ganger: ", investment_repeats_indefinetly())
print("Investeringene bare kan gjøres én gang og det er mangel på kapital: ", investment_only_once_with_limited_capital())