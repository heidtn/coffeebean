# coffeebean
The coffeebean uses a Punch Through Lighblue Bean (https://punchthrough.com/bean), a power switch tail II(https://www.sparkfun.com/products/10747), and any android phone to turn your boring old coffee maker into a fancy pants coffee machine that you can turn on without getting out of bed.  Awww yiss.

This is intended to be used with the el cheapo brand coffee makers that only have off/on options.  It will automatically shut off the coffee maker after a predetermined amount of time.

# getting started
You can build the android project from source (recommended), or you can use the unsigned .apk found in app/build/outputs/apk.  Then use Punch Throughs Bean Loader app to load the .ino file (found in beanfiles/coffeebean) to upload into your Bean (https://punchthrough.com/bean).  Connect a wire to D0 on to Bean to In+ on the power switch tail and GND on the Bean to In- on the tail.  Boom.  Ready to do the brew.

# use
Load your coffee maker with water and your best beans (not the lightblue one you fool).  Plug the power switch into the wall and the coffeemaker into the power switch.  Turn the coffee maker to the on position.  When you want to brew open your app and hit the button (literally the only button).  It will connect and ask you to press again to brew (ITS A FEATURE NOT A BUG (ok its a bug)).  Brewing will commence, the Bean will update your app when the coffee is ready (i.e. after a few minutes have passed). And stay warm for about 30 minutes before auto shutoff.

# known issues
The Android code was hastily put together in a few hours just to get things rolling, so theres a few bugs.  
  *Mainly if you turn bluetooth off and try to use the magic button it will act like its searching, but won't actually be working.  Simple fix, but I am lazy.
  *The app won't remember to reconnect to the Bean if in the middle of coffeemaking, pressing the button either starts the process or requests an update if the process is already started.
  *Have to hit the button again after connecting to properly send the start brew command. Could fix with a small delay, but I'm not entirely sure what the problem is.

