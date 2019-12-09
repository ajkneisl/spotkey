# SpotKey
SpotKey is a lightweight Spotify hot-key manager

## How to Use
Requirements:
- A Spotify account **with Premium**
- A Windows or Linux (untested) computer 

1. Execute the Jar
2. Focus on the newly opened browser window, and accept the Spotify oAuth page.
3. Copy the code at the redirected tab.
4. Paste the copied code into the SpotKey window.

SpotKey should now be active! To close or modify the configuration, look for the small icon in the task bar (Windows). 

## Configuration File
The configuration file is used for defining specific hotkeys.

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
