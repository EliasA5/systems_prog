from Repository import repo, Hat, Supplier, Order
import os
import sys


repo.create_tables()
with open(sys.argv[1], 'r') as config:
    firstLine = config.readline().strip()
    [numHats, numSuppliers] = list(map( lambda n: int(n), firstLine.split(',')))
    for i in range(0, numHats):
        l = config.readline().strip()
        repo.hats.insert(Hat(*l.split(',')))
    for i in range(0, numSuppliers):
        l = config.readline().strip()
        repo.suppliers.insert(Supplier(*l.split(',')))

with open(sys.argv[3], 'w') as output:
    with open(sys.argv[2], 'r') as ordersFile:
        i = 1
        for l in ordersFile:
            [location, topping] = l.strip().split(',')
            possibleHats = repo.hats.findAllWhereTopping(topping)
            if(len(possibleHats) == 0):
                continue
            hat = possibleHats[0]
            supplier = repo.suppliers.find(hat.supplier)
            newHat = Hat(hat.id, hat.topping, hat.supplier, hat.quantity - 1)
            repo.hats.modify_quantity(newHat)
            repo.orders.insert(Order(i, location, newHat.id))
            i += 1
            output.write(','.join([topping, supplier.name, location]) + '\n')
