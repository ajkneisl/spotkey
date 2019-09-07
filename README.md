# SpotKey
SpotKey is a lightweight Spotify hot-key manager

## How to Use
Execute the jar. It should then open your browser, and a window that should go over your browser. When you clcik accept on the Spotify page, it should redirect you to a place where you can view a code. Copy that code, and paste it into the opened window. **This requires Spotify Premium**

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
    ],
    "vars": []
}
```

The `use-default` indicates if you're using the default hot-keys. These consist of simple hot-keys such as `alt P` to pause. (view the bottom)

Actions are defined by Integers. The tasks are executed in the way they're put. Like in the example above, it'd go `0` then `1`. 

There can also be detailed actions. These are defined by the same integers but also have different values that modify it. Such as `3:incr|30`. Every time the keystroke is pressed, it would increase the volume by 30. If you wanted to something like `3:incr|30` and `3:decr|30`, do them in separate actions (["3:incr|30", "3:decr|30"]).

A keystroke should me in the format of *modifier* *key*. Like: `ctrl F`. Keys like `F` should be uppercase.

You can view all default actions here: https://github.com/Shoganeko/spotkey/blob/master/src/main/resources/default.json

If there's an issue that you know how to fix, please make a pull request. If you're confused, message me on discord at `sho#0001`.
