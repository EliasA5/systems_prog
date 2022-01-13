import sqlite3
import atexit
import sys
class Hat:
    def __init__(self,id,topping,supplier,quantity):
        self.id = id
        self.topping = topping
        self.supplier = supplier
        self.quantity = quantity
        
class Hats:
    def __init__(self,conn):
        self._conn = conn

    def insert(self,hat):
        self._conn.execute(""" INSERT INTO hats(id,topping,supplier,quantity) VALUES (?,?,?,?)""", (hat.id, hat.topping, hat.supplier, hat.quantity,))

    def find(self,hatId):
        c = self._conn.cursor()
        c.execute(""" SELECT id, topping, supplier, quantity FROM hats WHERE id = ? """, (hatId,))
        return Hat(*c.fetchone())

    def findAll(self):
        c = self._conn.cursor()
        all = c.execute(""" SELECT id, topping, supplier, quantity FROM hats """).fetchall()
        return [Hat(*hat) for hat in all]

    def findAllWhereTopping(self, topping):
        c = self._conn.cursor()
        all = c.execute(""" SELECT id, topping, supplier, quantity FROM hats WHERE topping = ? ORDER BY supplier""", (topping,)).fetchall()
        return [Hat(*hat) for hat in all]
    
    def modify_quantity(self, newHat):
        if(newHat.quantity == 0):
            self._conn.execute("""
            DELETE FROM hats WHERE id = ?
            """, (newHat.id,))
        else:
            self._conn.execute("""
            UPDATE hats
            SET quantity = ?
            WHERE id = ?
            """, (newHat.quantity, newHat.id))

class Supplier:
    def __init__(self,id,name):
        self.id = id
        self.name = name
        
class Suppliers:
    def __init__(self,conn):
        self._conn = conn

    def insert(self,supplier):
        self._conn.execute(""" INSERT INTO suppliers(id,name) VALUES (?,?)""",(supplier.id, supplier.name))

    def find(self,supplierId):
        c = self._conn.cursor()
        c.execute(""" SELECT id, name FROM suppliers WHERE id = ? """, (supplierId,))
        return Supplier(*c.fetchone()) 

    def findAll(self):
        c = self._conn.cursor()
        all = c.execute(""" SELECT id, name FROM suppliers """).fetchall()
        return [*all]           
        
class Order:
    def __init__(self,id,location,hat):
        self.id = id
        self.location = location
        self.hat = hat
        
class Orders:
    def __init__(self,conn):
        self._conn = conn

    def insert(self,order):
        self._conn.execute(""" INSERT INTO orders(id,location,hat) VALUES (?,?,?)""",(order.id, order.location,order.hat))  

    def find(self,orderId):
        c = self._conn.cursor()
        c.execute(""" SELECT id, location, hat FROM orders WHERE id = ? """, (orderId,))
        return Order(*c.fetchone())  

    def findAll(self):
        c = self._conn.cursor()
        all = c.execute(""" SELECT id, location, hat FROM orders """).fetchall()
        return [*all]         
    
class Repository:

    def __init__(self, repo_name):
        self.conn = sqlite3.connect(repo_name)
        self.hats = Hats(self.conn)
        self.suppliers = Suppliers(self.conn)
        self.orders = Orders(self.conn)

    def close_db(self):
        self.conn.commit()
        self.conn.close()

    def create_tables(self):
        self.conn.executescript("""
        CREATE TABLE hats(
            id INTEGER,
            topping STRING NOT NULL,
            supplier INTEGER,
            quantity INTEGER NOT NULL,
            PRIMARY KEY(id), FOREIGN KEY(supplier) REFERENCES suppliers(id)
        );
        CREATE TABLE suppliers(
            id INTEGER,
            name STRING NOT NULL,
            PRIMARY KEY(id)
        );
        CREATE TABLE orders(
            id INTEGER,
            location STRING NOT NULL,
            hat INTEGER,
            PRIMARY KEY(id), FOREIGN KEY(hat) REFERENCES hats(id)
        );
            """)
            
repo = Repository(sys.argv[4])
atexit.register(repo.close_db)