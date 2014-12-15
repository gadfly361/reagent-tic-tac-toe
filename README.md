# tic-tac-toe

Tic Tac Toe built with [Reagent](https://github.com/reagent-project/reagent).

[Demo](http://rc-tic-tac-toe.s3-website-us-west-1.amazonaws.com/)

Open `docs/uberdoc.html` for documentation.

## Usage

Create a javascript file from your clojurescript files.

```
$ lein cljsbuild once
```

Start a repl and then start the server.

```
$ lein repl

user=> (run!)
```

Open a browser and go to *localhost:8080*. You should see a Tic Tac Toe game, enjoy!
