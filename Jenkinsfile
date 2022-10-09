pipeline {
    agent any

    environment {
        GRADLE_ARGS = ''
        DISCORD_WEBHOOK = credentials('libraries-discord-webhook')
    }

    stages {
        stage('DiscordNotifyStart') {
            when {
                expression {
                    publishingToMaven()
                }
                not {
                    changeRequest()
                }
            }

            steps {
                script {
                    discord.sendStarted(currentBuild, DISCORD_WEBHOOK)
                }
            }
        }

        stage('Build') {
            steps {
                withGradle {
                    sh './gradlew ${GRADLE_ARGS} --refresh-dependencies --continue build'
                    gradleVersion(this)
                }
            }
        }

        stage('Publish') {
            when {
                expression {
                    publishingToMaven()
                }
                not {
                    changeRequest()
                }
            }

            steps {
                withCredentials([usernamePassword(credentialsId: 'hundred-media-maven-user', usernameVariable: 'MAVEN_USER', passwordVariable: 'MAVEN_PASSWORD')]) {
                    withGradle {
                        sh './gradlew ${GRADLE_ARGS} publish'
                    }
                }
            }
        }
    }

    post {
        always {
            script {
                if (env.CHANGE_ID/* Pull Request ID */ == null && publishingToMaven()) {
                    discord.sendFinished(currentBuild, DISCORD_WEBHOOK)
                }
            }
        }
    }
}