# Kenwood GPS Converter

**This software is intended to be used with Kenwood radios. The software is neither built nor maintained by Kenwood.**

Java command line tool to convert received position reports to a common format for further processing.
The converter supports the Kenwood proprietary PKNDS format which is usually received via
the serial port of a radio.
The parsed position report can be printed to the console, recorded to GPX tracks or sent to a
[Geobroker](https://github.com/wrk-fmd/geobroker) instance.

## Why PKNDS Format?

The PKNDS format contains beside the GPRMC data also the unit ID of the radio.
This information is needed to filter the data by sending radio.

The PKNDS format is described for example in the NX-5000 manual:

> Upon receipt of the GPS data, the base station transceiver creates the $PKNDS data which is the
> KENWOOD proprietary sentence from the received GPS data and sends the created data from
> the communication port.
> The $PKNDS data contains the $GPRMC data in the NMEA-0183 format, Unit ID, and the status
> information.

## Usage

[Build](#build) the java package (including dependencies) or download the latest release.
Example configuration for geobrokoer mode and a script to run the application is available in the `scripts/` folder.

The converter supports three modes, which can be enabled independently:
* Streaming mode: The parsed position report is printed to the command line.
  The output format is a human readable representation in Java toString() format of apache commons.
* Geobroker mode: The parsed position report is used to look up the unit in the provided geobroker configuration file.
  If a unit with a matching unit ID is found, the position report is sent to the geobroker.
  Numerical-only unit IDs (e.g. provided in dPMR) are sanitized by cutting leading zeros.
  Unit IDs with non-numerical characters are handled as-is (e.g. NXDN).
* GPX tracking mode: The converter writes a GPX track per unit to the provided directory.
  The track is written on 600 position reports or after 12 hours, depending which trigger comes first.
  At the moment this is only configurable by changing the code in `CommandLineBootstrapper`.

The tool expects the serial data of the radio on standard input.
You can directly pipe the serial data from the device to program with a program of your choice,
for example by `unbuffer cat /dev/ttyUSB0 | java -jar ...`.
You can also convert already recorded serial output by `cat recorded-serial-data.txt | java -jar ...`.
For a full working example of streaming the serial data see
[scripts/linux/start-geobroker-mode.sh](scripts/linux/start-geobroker-mode.sh).

### Parameters

At least one operation mode must be selected.

     -d,--gpx-directory <arg>    Output directory for GPX tracks.
     -g,--geobroker-mode <arg>   Start in Geobroker mode. Input from stdin is
                                 parsed and sent to the configured Geobroker
                                 server. Configuration JSON file must be
                                 provided as argument.
     -i,--ignore-timestamp       Ignore timestamps sent by the radio and
                                 generate on server.
     -s,--streaming-mode         Start in streaming mode. Input from stdin is
                                 parsed and printed out to stdout.

### Step-by-step installation on Raspberry Pi

To run the pre-built kenwood-gps program on a Raspberry Pi, following steps are necessary:

* Install Java 17 JRE and the package `expect` (required for the `unbuffer` command in the startup script)

```shell script
sudo apt-get install openjdk-17-jre-headless expect
``` 

* Create a directory to download the release. `kenwood-gps` is used in the scripts.
  If you prefer another name, update the scripts manually!

```shell script
mkdir ~/kenwood-gps
cd ~/kenwood-gps

wget https://github.com/robo-w/kenwood-gps-converter/releases/download/v1.3.1/kenwood-gps-converter-1.3.1.jar
wget https://github.com/robo-w/kenwood-gps-converter/releases/download/v1.3.1/scripts.zip
```

* Unzip the scripts, and make the program and the scripts executable.

```shell script
unzip scripts.zip

chmod +x kenwood-gps-converter-1.3.1.jar
chmod +x linux/start-geobroker-mode.sh
chmod +x linux/set_baud_rate_8N1.sh
```

* Adapt the geobroker configuration file. An example configuration is part of the scripts.zip.
  The resulting file must be named `geobroker-config.js` to be found by the startup script.

```shell script
mv example-configuration.json geobroker-config.js # Adapt example or use your own configuration file.
```

* Move scripts to home directory for easy access.

```shell script
mv linux/*.sh ~/
```

* Run program manually.

```shell script
~/start-geobroker-mode.sh /dev/ttyUSB0 # manually start program with first USB-to-serial converter
```

* (Optional) Add following lines to `rc.local` to start the program on boot.

```shell script
printf "Starting kenwood-gps-converter"
sudo su pi -c '/home/pi/start-geobroker-mode.sh /dev/ttyUSB0' &
```

## Build

Checkout and build the referenced version of [Geobroker Client Library](https://github.com/robo-w/geobroker-client-lib).

Run `mvn clean install` to build package.
The resulting `kenwood-gps-converter-<version>-jar-with-depencies.jar` is the
final version including all bundled dependencies.

## License

This software is available under MIT license.
See [License file](LICENSE) for further details.

## Disclaimer

This software is not affiliated with Kenwood or JVCKENWOOD.
The software is built for converting data usually provided by Kenwood radios.
Parsing the data is done according to the referenced standards in user manuals.
