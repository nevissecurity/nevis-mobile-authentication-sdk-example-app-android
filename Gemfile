source "https://rubygems.org"

gem "fastlane"
gem "java-properties"

group :development do
    gem "rubocop", "~> 1.63", require: false
    gem "team_fastlane-rubocop", "~> 1.2.10", require: false
end

plugins_path = File.join(File.dirname(__FILE__), "fastlane", "Pluginfile")
eval_gemfile(plugins_path) if File.exist?(plugins_path)
