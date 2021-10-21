# Viessmann Binding

_This binding connects Viessmann Heatings via the new Viessmann API._

_It provides features like the ViCare-App._

## Supported Things

The binding supports the following thing types:

* `bridge` - Supports connection to the Viesmmann API.
* `device` - Provides a device which is connected (Discovery)

## Discovery

_It discover all devices wich are connected through your account._

## Binding Configuration

The `bridge` thing supports the connection to the Viessmann API.

* `API Key` (required) The API Key form the Viessman developer portal 
* `user` (required) The E-Mail address which is registered for the ViCare App
* `password` (required) The password which is registered for the ViCare App
* `installationId` (optional / it will be discovered) The installation Id which belongs to your installation 
* `gatewaySerial` (optional / it will be discovered) The gateway serial which belongs to your installation
* `apiTimeout` (default = 22) Time in seconds to allow API request to complete
* `pollingInterval` (default = 120) How often the available heating should be queried in seconds. If there are more devices, you have to increase the time.

## Thing Configuration

_All configurations are made in the UI_

## Channels

_There are many different channels. The channels are automaticaly generaten for all available fetaures_

## Full Example

_TODO_
