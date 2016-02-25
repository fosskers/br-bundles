Bundle Pricing
==============

Started: 2016 February 25 @ 10:30
Finished: 

lunch at 2016 February 25 @ 11:20
finished lunch at 2016 February 25 @ 12:09
also did adenda crap for a while (12:16 - 12:33)

### Problem
Given a catalogue of items, a set of possible discount "bundles"
for those items, and a customer order, produce the lowest possible total
price that could be assigned to the collection.

### Thoughts
- This is a fairly classic dynamic programming problem, of a similar vein
to the backpack problem.
- At "it should be able to handle simultaneous calls" one might think "I bet
they want me to use Akka!", especially given that BoldRadius is a Typesafe
stack shop. Akka would be overkill here, as this entire library can
be handled with stateless pure functions (i.e. the less moving parts,
the better).
- "The API is initialized with..." will be ignored, given that we
strive for a stateless system. With no state, there is also no
worry about simultaneous access to the catelogue, mutability issues, etc.
- "Initialization" of the catalogue and bundles is offset to the API caller.
This has the advantage of allowing both to be alterable at any time.
  
### Methods
Being a dynamic programming problem, we could go about this a number of
ways. Two ideas are:
- Process in serial through the `Item` list, within a `State` Monad to keep
track of already-calculated bundle combinations as we move through the
recursion tree.
- Process in parallel with `Future`s across the `Item` list, making the
assumptions that: 
  1. given enough threads on the server, this parallel approach
  will out-speed the serial variant, despite the possibility of repeated
  calculation farther down the recursion tree.
  2. custom orders will be relatively small (no statement otherwise was
  given in the problem spec).

I've opted for the latter.

### Tools
* Vanilla Scala
* Activator
* Emacs with [ENSIME](https://github.com/ensime)

### Random
- We need a `BundleMap` which maps `Item` to the possible bundles it can be in.
- Only need to map across each `Item` type once (but still include repeats
in the full set of items to be bundled)
