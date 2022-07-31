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
      "dueDate": "2023-01-01",
      "description": "demo",
      "requiredCKBPercentage": 30
    },
    {
      "dueDate": "2023-07-01",
      "description": "early release",
      "requiredCKBPercentage": 70
    },
    {
      "dueDate": "2023-12-01",
      "description": "final release",
      "requiredCKBPercentage": 100
    }
  ],
  "targetCKB": 500000,
  "deliveries": {
    "10000": "discount",
    "100000": "free game"
  },
  "description": "the best game",
  "name": "a project"
}'
```

See [ProjectController.java](./src/main/java/com/example/crowdfunding/controller/ProjectController.java) for other REST APIs.
