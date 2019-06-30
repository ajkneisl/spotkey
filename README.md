# spotkey
SpotKey is a lightweight Spotify hot-key manager.
## How to Use
If you don't know how to build this, I suggest waiting until something is released.

When starting, you're currently required to use command line. It will open your browser, to which then you're required to accept. Then it will redirect you. You need to copy everything after the `code=` and then paste that into the client. __This requires Spotify Premium to work properly.__

You can define specific keystrokes in the SpotKey configuration folder. If you're on Windows, you can expect to find this at `%appdata%/spotkey/conf.json`. On Linux, this is at `/etc/spotkey/conf.json`. 

The format for the configuration file should be 

```
{
    "use-default": true,
    "keys": [
        {
            "keystroke": "shift ctrl N",
            "actions": [0, 1]
        }
    ]
}
```

The `use-default` indicates if you're using the default hot-keys. These consist of simple hot-keys such as `CTRL SHIFT N` for next song. (view the bottom)

Actions are defined by Integers. The tasks are executed in the way they're put. Like in the example above, it'd go `0` then `1`. 

There can also be detailed actions. These are defined by the same integers but also have different values that modify it. Like `3:incr|30`. Every time the keystroke is pressed, it would increase the volume by 30. If you wanted to something like `3:incr|30` and `3:decr|30`, do them in separate actions.

A keystroke should me in the format of *modifier* *key*. Like: `ctrl F`. Keys, like `F`, should be uppercase.

If there's an issue that you know how to fix, please make a pull request. If you're confused, message me on discord at `shozer#0001`.