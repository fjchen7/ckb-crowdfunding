## Run

Execute `./gradlew bootRun` under project root.

## REST API

Create a new crowdfunding project with REST API.

```shell
curl -X "POST" "http://localhost:8080/projects" \
     -H 'content-type: application/json' \
     -d $'{
  "creatorAddress": "ckt1qzda0cr08m85hc8jlnfp3zer7xulejywt49kt2rr0vthywaa50xwsqvglkprurm00l7hrs3rfqmmzyy3ll7djdsujdm6z",
  "endDate": "2023-12-31",
  "startDate": "2022-08-15",
  "milestones": [
    {
      "description": "demo",
      "approvalRatioThreshold": 70,
      "targetCKBPercentage": 30,
      "dueDate": "2023-01-01"
    },
    {
      "description": "early release",
      "approvalRatioThreshold": 70,
      "targetCKBPercentage": 70,
      "dueDate": "2023-07-01"
    },
    {
      "description": "final release",
      "approvalRatioThreshold": 70,
      "targetCKBPercentage": 100,
      "dueDate": "2023-12-01"
    }
  ],
  "targetCKB": 500000,
  "deliveries": {
    "10000": {
      "reward": "discount"
    },
    "100000": {
      "reward": "free game"
    }
  },
  "description": "the best game",
  "name": "a project",
  "startupCKB": 100000
}'
```

Pledge to project

```shell
curl -X "POST" "http://localhost:8080/projects/1/pledge" \
     -H 'content-type: application/json' \
     -d $'{
  "privateKey": "0xa0f2d51b7c9c2a21736617285115a42a124447f277b6df61370ab996583b169f",
  "pledgedCKB": 1000
}'
```

See [ProjectController.java](src/main/java/com/example/crowdfunding/controller/Controller.java) for other REST APIs.
