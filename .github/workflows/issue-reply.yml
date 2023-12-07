name: Issue Reply

on:
  issues:
    types: [labeled]

jobs:
  reply-helper:
    runs-on: ubuntu-latest
    steps:
      - name: feature
        if: github.event.label.name == 'feature' || github.event.label.name == 'fix'
        uses: actions-cool/issues-helper@v3
        with:
          actions: 'create-comment'
          token: ${{ secrets.GITHUB_TOKEN }}
          issue-number: ${{ github.event.issue.number }}
          body: |
            Hello @${{ github.event.issue.user.login }}. We are very happy to see your proposal/feedback, we will review and process it as soon as possible, and PRs are very welcome.

            你好 @${{ github.event.issue.user.login }}，我们非常高兴看到你的提议/反馈，我们会尽快查看和处理，并且非常欢迎PR。

      - name: bug
        if: github.event.label.name == 'bug'
        uses: actions-cool/issues-helper@v3
        with:
          actions: 'create-comment'
          token: ${{ secrets.GITHUB_TOKEN }}
          issue-number: ${{ github.event.issue.number }}
          body: |
            Hello @${{ github.event.issue.user.login }}. We would love to see your questions for our project, we will review and address them as soon as possible, and PRs are very welcome.

            你好 @${{ github.event.issue.user.login }}，我们非常乐意看到你为我们的项目提出的问题，我们会尽快查看和处理，并且非常欢迎PR。

      - name: question
        if: github.event.label.name == 'question'
        uses: actions-cool/issues-helper@v3
        with:
          actions: 'create-comment'
          token: ${{ secrets.GITHUB_TOKEN }}
          issue-number: ${{ github.event.issue.number }}
          body: |
            Hello @${{ github.event.issue.user.login }}. We'll be happy to answer your questions. Just a moment.

            你好 @${{ github.event.issue.user.login }}，我们非常乐意为你解答提出的问题，稍等。
