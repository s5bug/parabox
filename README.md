# parabox

An attempt at a reimplementation of the Patrick's Parabox state machine.

This reimplementation's purpose is to aid in understanding how to port
Patrick's Parabox to less powerful platforms (of interest are Fancade and
SmileBASIC 4).

## running the Indigo frontend

```shell
sbt game/fastOptJS
sbt game/indigoBuild
```

Then open `./game/target/indigoBuild/index.html`.
