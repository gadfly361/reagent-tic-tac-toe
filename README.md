# tic-tac-toe

Tic Tac Toe built with [Reagent](https://github.com/reagent-project/reagent).  [Demo](http://rc-tic-tac-toe-mm.s3-website-us-west-1.amazonaws.com/)

The game employs a minimax ai on the 3x3 board, but reverts to a heuristic ai for larger boards.

## Usage

Create a javascript file from your clojurescript files.

```
$ lein cljsbuild once
```

*Note: the speclj tests should run, and this requires phantomjs.*

Start a repl and then start the server.

```
$ lein repl

user=> (run)
```

Open a browser and go to *localhost:10555*. You should see a Tic Tac Toe game, enjoy!
