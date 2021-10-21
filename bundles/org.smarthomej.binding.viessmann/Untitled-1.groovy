{
    "viErrorId": "req-52c69536e2034805a5be7f3969a2c56d",
    "statusCode": 404,
    "errorType": "INSTALLATION_NOT_FOUND",
    "message": "INSTALLATION_NOT_FOUND",
    "extendedPayload": {}
}

{
    "viErrorId": "req-4701bb46d29848978c1396639de4370c",
    "statusCode": 404,
    "errorType": "GATEWAY_NOT_FOUND",
    "message": "GATEWAY_NOT_FOUND",
    "extendedPayload": {}
}

{
    "viErrorId": "req-d9523a1ed37646c3b948d559f822cc61",
    "statusCode": 404,
    "errorType": "NOT_FOUND",
    "message": "NOT_FOUND",
    "extendedPayload": {}
}

{
    "error": "invalid-token-request"
}

{"error":"Client not registered."}


https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices

{
    "data": [
        {
            "gatewaySerial": "7555115501845105",
            "id": "0",
            "boilerSerial": "7554201501756105",
            "boilerSerialEditor": "DeviceCommunication",
            "bmuSerial": "7555113502390105",
            "bmuSerialEditor": "DeviceCommunication",
            "createdAt": "2018-06-07T21:15:45.512Z",
            "editedAt": "2021-10-03T21:24:33.551Z",
            "modelId": "VScotHO1_200",
            "status": "Online",
            "deviceType": "heating",
            "roles": [
                "type:boiler"
            ]
        }
    ]
}

13:51:45.343 [INFO ] [hab.event.ThingStatusInfoChangedEvent] - Thing 'viessmann:bridge:906e698a42' changed from UNINITIALIZED (HANDLER_MISSING_ERROR) to INITIALIZING
13:51:45.348 [DEBUG] [ternal.handler.ViessmannBridgeHandler] - Initialize Viessmann Accountservice
13:51:45.366 [DEBUG] [g.viessmann.internal.api.ViessmannApi] - API: Creating OAuth Client Service for viessmann:bridge:906e698a42
13:51:45.368 [DEBUG] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: State is NEED_AUTH: Executing step: 'authorize'
13:51:45.368 [TRACE] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: Getting authorize URL=https://iam.viessmann.com/idp/v2/authorize?response_type=code&client_id=32800f497cce04513af2ddda0a893b72&code_challenge=2e21faa1-db2c-4d0b-a10f-575fd372bc8c-575fd372bc8c&redirect_uri=http://localhost:8080/viessmann-api/authcode/&scope=IoT%20User%20offline_access
13:51:45.405 [TRACE] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: Auth response: null
13:51:45.407 [DEBUG] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: Got null authorize response from Viessmann API
13:51:45.407 [DEBUG] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: State is NEED_AUTH: Executing step: 'getToken'
13:51:45.408 [TRACE] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: Posting token URL=https://iam.viessmann.com/idp/v2/token?grant_type=authorization_code&client_id=32800f497cce04513af2ddda0a893b72&redirect_uri=http://localhost:8080/viessmann-api/authcode/&code_verifier=2e21faa1-db2c-4d0b-a10f-575fd372bc8c-575fd372bc8c&code=null
13:51:45.620 [DEBUG] [.viessmann.internal.api.ViessmannAuth] - BAD REQUEST(400) response received: {"error":"invalid-token-request"}
13:51:45.621 [TRACE] [.viessmann.internal.api.ViessmannAuth] - ViessmannAuth: Got a valid token response: {"error":"invalid-token-request"}
13:51:45.622 [INFO ] [g.viessmann.internal.api.ViessmannApi] - API: Got exception trying to get access token from OAuth service
java.lang.NullPointerException: null
	at org.smarthomej.binding.viessmann.internal.api.ViessmannAuth.getTokens(ViessmannAuth.java:231) ~[bundleFile:?]
	at org.smarthomej.binding.viessmann.internal.api.ViessmannAuth.doAuthorization(ViessmannAuth.java:112) ~[bundleFile:?]
	at org.smarthomej.binding.viessmann.internal.api.ViessmannApi.isAuthorized(ViessmannApi.java:228) [bundleFile:?]
	at org.smarthomej.binding.viessmann.internal.api.ViessmannApi.<init>(ViessmannApi.java:145) [bundleFile:?]
	at org.smarthomej.binding.viessmann.internal.handler.ViessmannBridgeHandler.initialize(ViessmannBridgeHandler.java:121) [bundleFile:?]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method) ~[?:?]
	at jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62) ~[?:?]
	at jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43) ~[?:?]
	at java.lang.reflect.Method.invoke(Method.java:566) ~[?:?]
	at org.openhab.core.internal.common.AbstractInvocationHandler.invokeDirect(AbstractInvocationHandler.java:154) [bundleFile:?]
	at org.openhab.core.internal.common.Invocation.call(Invocation.java:52) [bundleFile:?]
	at java.util.concurrent.FutureTask.run(FutureTask.java:264) [?:?]
	at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128) [?:?]
	at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628) [?:?]
	at java.lang.Thread.run(Thread.java:834) [?:?]
13:51:45.623 [TRACE] [g.viessmann.internal.api.ViessmannApi] - API: Get Request URL is 'https://api.viessmann.com/iot/v1/equipment/installations/77/gateways/7555115501845105/devices/0/features/'
13:51:45.624 [INFO ] [g.viessmann.internal.api.ViessmannApi] - API: Unable to execute GET: Can not set auth header because access token is null
13:51:45.624 [DEBUG] [ternal.handler.ViessmannBridgeHandler] - Refresh job scheduled to run every days. for 'viessmann:bridge:906e698a42'

4:03:04.990 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0 Commands: 
{
  "setName": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0/commands/setName",
    "name": "setName",
    "isExecutable": false,
    "params": {
      "name": {
        "required": true,
        "type": "string",
        "constraints": {
          "minLength": 1,
          "maxLength": 20
        }
      }
    }
  }
}
04:03:04.995 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.heating.curve Commands: 
{
  "setCurve": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.heating.curve/commands/setCurve",
    "name": "setCurve",
    "isExecutable": true,
    "params": {
      "slope": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 0.2,
          "max": 3.5,
          "stepping": 0.1
        }
      },
      "shift": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": -13,
          "max": 40,
          "stepping": 1
        }
      }
    }
  }
}
04:03:04.999 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.heating.schedule Commands: 
{
  "setSchedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.heating.schedule/commands/setSchedule",
    "name": "setSchedule",
    "isExecutable": true,
    "params": {
      "newSchedule": {
        "required": true,
        "type": "Schedule",
        "constraints": {
          "maxEntries": 4,
          "resolution": 10,
          "modes": [
            "normal"
          ],
          "defaultMode": "reduced"
        }
      }
    }
  }
}
04:03:05.002 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.modes.active Commands: 
{
  "setMode": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.modes.active/commands/setMode",
    "name": "setMode",
    "isExecutable": true,
    "params": {
      "mode": {
        "required": true,
        "type": "string",
        "constraints": {
          "enum": [
            "dhw",
            "dhwAndHeating",
            "forcedNormal",
            "forcedReduced",
            "standby"
          ]
        }
      }
    }
  }
}04:03:05.006 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.programs.comfort Commands: 
{
  "setTemperature": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.comfort/commands/setTemperature",
    "name": "setTemperature",
    "isExecutable": true,
    "params": {
      "targetTemperature": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 4,
          "max": 37,
          "stepping": 1
        }
      }
    }
  },
  "activate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.comfort/commands/activate",
    "name": "activate",
    "isExecutable": true,
    "params": null
  },
  "deactivate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.comfort/commands/deactivate",
    "name": "deactivate",
    "isExecutable": false,
    "params": null
  }
}04:03:05.011 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.programs.eco Commands: 
{
  "activate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.eco/commands/activate",
    "name": "activate",
    "isExecutable": false,
    "params": null
  },
  "deactivate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.eco/commands/deactivate",
    "name": "deactivate",
    "isExecutable": false,
    "params": null
  }
}04:03:05.013 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.programs.holiday Commands: 
{
  "changeEndDate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.holiday/commands/changeEndDate",
    "name": "changeEndDate",
    "isExecutable": false,
    "params": {
      "end": {
        "required": true,
        "type": "string",
        "constraints": null
      }
    }
  },
  "schedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.holiday/commands/schedule",
    "name": "schedule",
    "isExecutable": true,
    "params": {
      "start": {
        "required": true,
        "type": "string",
        "constraints": null
      },
      "end": {
        "required": true,
        "type": "string",
        "constraints": {}
      }
    }
  },
  "unschedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.holiday/commands/unschedule",
    "name": "unschedule",
    "isExecutable": true,
    "params": {}
  }
}04:03:05.014 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.programs.normal Commands: 
{
  "setTemperature": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.normal/commands/setTemperature",
    "name": "setTemperature",
    "isExecutable": true,
    "params": {
      "targetTemperature": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 3,
          "max": 37,
          "stepping": 1
        }
      }
    }
  }
}04:03:05.017 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.circuits.0.operating.programs.reduced Commands: 
{
  "setTemperature": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.circuits.0.operating.programs.reduced/commands/setTemperature",
    "name": "setTemperature",
    "isExecutable": true,
    "params": {
      "targetTemperature": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 3,
          "max": 37,
          "stepping": 1
        }
      }
    }
  }
}04:03:05.020 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.dhw.oneTimeCharge Commands: 
{
  "activate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.oneTimeCharge/commands/activate",
    "name": "activate",
    "isExecutable": true,
    "params": null
  },
  "deactivate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.oneTimeCharge/commands/deactivate",
    "name": "deactivate",
    "isExecutable": false,
    "params": null
  }
}04:03:05.023 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.dhw.pumps.circulation.schedule Commands: 
{
  "setSchedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.pumps.circulation.schedule/commands/setSchedule",
    "name": "setSchedule",
    "isExecutable": true,
    "params": {
      "newSchedule": {
        "required": true,
        "type": "Schedule",
        "constraints": {
          "maxEntries": 4,
          "resolution": 10,
          "modes": [
            "on"
          ],
          "defaultMode": "off"
        }
      }
    }
  }
}04:03:05.033 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.dhw.schedule Commands: 
{
  "setSchedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.schedule/commands/setSchedule",
    "name": "setSchedule",
    "isExecutable": true,
    "params": {
      "newSchedule": {
        "required": true,
        "type": "Schedule",
        "constraints": {
          "maxEntries": 4,
          "resolution": 10,
          "modes": [
            "on"
          ],
          "defaultMode": "off"
        }
      }
    }
  }
}04:03:05.039 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.dhw.temperature Commands: 
{
  "setTargetTemperature": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.temperature/commands/setTargetTemperature",
    "name": "setTargetTemperature",
    "isExecutable": true,
    "params": {
      "temperature": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 10,
          "max": 60,
          "stepping": 1
        }
      }
    }
  }
}04:03:05.041 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.dhw.temperature.main Commands: 
{
  "setTargetTemperature": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.dhw.temperature.main/commands/setTargetTemperature",
    "name": "setTargetTemperature",
    "isExecutable": true,
    "params": {
      "temperature": {
        "required": true,
        "type": "number",
        "constraints": {
          "min": 10,
          "max": 60,
          "stepping": 1
        }
      }
    }
  }
}04:03:05.046 [TRACE] [smann.internal.handler.HeatingHandler] - Feature: heating.operating.programs.holiday Commands: 
{
  "changeEndDate": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.operating.programs.holiday/commands/changeEndDate",
    "name": "changeEndDate",
    "isExecutable": false,
    "params": {
      "end": {
        "required": true,
        "type": "string",
        "constraints": null
      }
    }
  },
  "schedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.operating.programs.holiday/commands/schedule",
    "name": "schedule",
    "isExecutable": true,
    "params": {
      "start": {
        "required": true,
        "type": "string",
        "constraints": null
      },
      "end": {
        "required": true,
        "type": "string",
        "constraints": {}
      }
    }
  },
  "unschedule": {
    "uri": "https://api.viessmann.com/iot/v1/equipment/installations/775/gateways/7555115501845105/devices/0/features/heating.operating.programs.holiday/commands/unschedule",
    "name": "unschedule",
    "isExecutable": true,
    "params": {}
  }
}