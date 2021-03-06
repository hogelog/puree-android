Puree
====

## Description

Puree is a data collector for unified logging layer, which provides some functions like below

- Filtering
 - Enable to interrupt process before sending log.
- Buffering
 - Store logs to buffer until log was sent.
- Batching
 - Enable to send logs by 1 request.
- Retrying
 - Retry to send logs after buckoff time automatically if sending logs fails.

![](./images/logging.png)

## Usage

### Initializing

Configure Puree on application created.

```java
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        Puree.initialize(buildConfiguration(this));
    }

    public static PureeConfiguration buildConfiguration(Context context) {
        PureeFilter addEventTimeFilter = new AddEventTimeFilter();

        return new PureeConfiguration.Builder(context)
                .registerOutput(new OutLogcat())
                .registerOutput(new OutBufferedLogcat(), addEventTimeFilter)
                .build();
    }
}
```

### Sending logs

Log class should implements JsonConvertible interface.

```java
public class ClickLog extends JsonConvertible {
    @SerializedName("page")
    private String page;
    @SerializedName("label")
    private String label;

    public ClickLog(String page, String label) {
        this.page = page;
        this.label = label;
    }
}
```

Send log to Puree in an arbitrary timing.

```java
Puree.send(new ClickLog("MainActivity", "Hello"), OutLogcat.TYPE);
```

### Testing

LogSpec provides utilities for tests.

```java
public class ClickLogTest extends AndroidTestCase {
    public void testFormat() {
        new LogSpec(PureeConfigurator.buildConf(getContext()))
                .log(new ClickLog("MainActivity", "ClickLog1"), OutLogcat.TYPE)
                .log(new ClickLog("MainActivity", "ClickLog2"), OutLogcat.TYPE)
                .targetType(OutLogcat.TYPE)
                .shouldBe(new LogSpec.Matcher() {
                    @Override
                    public void expect(JSONArray serializedLogs) throws JSONException {
                        assertEquals(2, serializedLogs.length());
                        JSONObject serializedLog = serializedLogs.getJSONObject(0);
                        assertEquals("MainActivity", serializedLog.getString("page"));
                        assertEquals("ClickLog1", serializedLog.getString("label"));
                        assertTrue(serializedLog.has("event_time"));
                    }
                });
    }
}
```

### Create output plugins


There are two types of output plugins: Non-Buffered, Buffered.

- Non-Buffered output plugins do not buffer data and immediately write out results.

![](./images/output_plugin.png)

- Buffered output plugins store logs to local storage temporary.

![](./images/buffered_output_plugin.png)

You can create a plugin by inheriting Puree.Output or Puree.BufferedOutput. See example plugins below.

- [OutLogcat](https://github.com/rejasupotaro/Puree/blob/master/plugins%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcookpad%2Fandroid%2Fpuree%2Fplugins%2FOutLogcat.java)
- [OutBufferedLogcat](https://github.com/rejasupotaro/Puree/blob/master/plugins%2Fsrc%2Fmain%2Fjava%2Fcom%2Fcookpad%2Fandroid%2Fpuree%2Fplugins%2FOutBufferedLogcat.java)

## Install


Clone this repository in your PC and compile with your project for now.
I'll upload Puree to maven central sooner or later.

```java
// settings.gradle
include ':app', ':..:puree-android:puree', ':..:puree-android:plugins'

// app/build.gradle
compile project(':..:puree-android:puree')
compile project(':..:puree-android:plugins')
```
