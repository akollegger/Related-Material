Transactions
============

All graph write operations must be performed within a transaction. 
Transactions are thread confined and can be nested as "_flat nested 
transactions_" --- all nested transactions are added to the scope 
of a top level transaction. Any nested transaction can mark the 
containing top level transaction for rollback, meaning the entire 
transaction will be rolled back. It is not possible to rollback 
changes made only in a nested transaction.

When working with transactions the interaction cycle looks like this:

1. Begin a transaction
2. Operate on the graph performing write operations
3. Mark the transaction as successful or not
4. Finish the transaction

It is very important to finish each transaction since failing to
do so the transaction will not release the locks or memory it has
acquired. The idiomatic use of transactions in Neo4j is to use a
`try-finally` block, starting the transaction and then try to perform
the write operations. The last operation in the try block should
mark the transaction as successful while the finally block should
finish the transaction (commit or rollback the transaction depending
on success status).

All modifications performed in a transaction are kept in memory so
for very large updates they have to be split into several top level
transaction to avoid running out of memory. It must be a top level
transaction since splitting up the work in many nested transactions
will just add all the work to the top level transaction.

In an environment that makes use of thread pooling other errors may
occur when failing to finish a transaction properly. Consider a
leaked transaction that did not get finished properly, it will be
tied to a thread and when that thread gets scheduled to perform
work starting a new (what looks to be a) top level transaction it
will actually be a nested transaction. If the leaked transaction
state is "marked for rollback" (will happen if a deadlock was
detected) no more work can be performed on that transaction, trying
to do so will result in error on each call to a write operation.

Isolation levels
----------------

By default a read operation will read the last committed value
unless a local modification within the current transaction exist.
The default isolation level is very similar to `READ_COMMITTED`, reads
do not block or take any locks so non-repeatable reads can occur.
It is possible to achieve stronger isolation level (such as
`REPETABLE_READ` and `SERIALIZABLE`) by manually acquiring read and
write locks.

The default lock behavior is:	

* When adding, changing or removing a property on a node or relationship 
  a write lock will be taken on the specific node or relationship. 	
* When creating or deleting a node a write lock will be taken for the specific node. 	
* When creating or deleting a relationship a write lock will be taken on the 
  specific relationship and both its nodes. The locks will be added to the transaction 
  and released when the transaction finishes.

Deadlocks
---------

Since locks are used it is possible for deadlocks to happen. Neo4j
will however detect any deadlock (caused by acquiring a lock) before
they happen and throw an exception. Before the exception is thrown
the transaction is marked for rollback. All locks acquired by the
transaction are still being held but will be released when the
transaction is finished (in the finally block as pointed out earlier).
Once the locks are released other transactions that were waiting
for locks held by the transaction causing the deadlock can proceed.
The work performed by the transaction causing the deadlock can then
be retried by the user if needed.

Experiencing frequent deadlocks is an indication of concurrent write
requests happening in such a way that it is not possible to execute
while at the same time live up to the intended isolation and
consistency. The solution is to make sure concurrent updates happen
in a reasonable way. For example given two specific nodes (A and
B), adding or deleting relationships to both these nodes in random
order for each transaction will result in deadlocks when there are
two or more transactions doing that concurrently. The solution is
to make sure updates always happens in the same order (first A then
B). Another solution is to make sure each thread/transaction does
not have any conflicting writes to a node or relationship as some
other concurrent transaction (example, let a single thread do all
updates of a specific type).

Deadlocks caused by the use of other synchronization than the locks
managed by Neo4j can still happen. Since all operations in the Neo4j
API are to be considered thread safe there is no need for external
synchronization. Other code that requires synchronization should
be synchronized in such a way that it never performs any Neo4j
operation in the synchronized block.  Delete semantics

When deleting a node or a relationship all properties for that
entity will be automatically removed but the relationships of a
node will not be removed. Neo4j enforces a constraint (upon commit)
that all relationships must have a valid start and end node. In
effect this means that trying to delete a node that still has
relationships attached will throw an exception upon commit. It is
however possible to choose in which order to delete the node and
the attached relationships as long as no relationships exist when
the transaction is committed. Delete semantics can be summarized
in the following bullets:

All properties of a node or relationship will be removed when deleted
A deleted node can not have any attached relationships when the
transaction commits It is possible to acquire a reference to a
deleted relationship or node that has not yet been committed Any
write operation on a node or relationship after it has been deleted
(but not yet committed) will throw an exception After commit trying
to acquire a new or work with an old reference to a deleted node
or relationship will throw an exception

