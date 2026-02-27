{
  pkgs,
  lib,
  config,
  inputs,
  ...
}:

{
  # https://devenv.sh/basics/
  env = {
    JAVA_HOME = lib.mkForce "${config.languages.java.jdk.package}";
    OTEL_EXPORTER_OTLP_ENDPOINT = "http://localhost:4318";
    OTEL_EXPORTER_OTLP_PROTOCOL = "http/protobuf";
    OTEL_TRACES_SAMPLER = "always_on";
  };

  # https://devenv.sh/packages/
  packages = [ pkgs.git ];

  # https://devenv.sh/languages/
  # languages.rust.enable = true;
  languages = {
    java = {
      enable = true;
      jdk.package = pkgs.jdk25;
      gradle = {
        enable = true;
        package = pkgs.gradle_9;
      };
    };
  };

  services = {
    opentelemetry-collector = {
      enable = true;
      settings = {
        receivers = {
          otlp = {
            protocols = {
              grpc.endpoint = "0.0.0.0:4317";
              http.endpoint = "0.0.0.0:4318";
            };
          };
        };
        processors = {
          batch = { };
        };
        exporters = {
          prometheus = {
            endpoint = "0.0.0.0:9464";
          };
          debug = {
            verbosity = "detailed";
          };
        };
        service = {
          pipelines = {
            traces = {
              receivers = [ "otlp" ];
              processors = [ "batch" ];
              exporters = [ "debug" ];
            };
            metrics = {
              receivers = [ "otlp" ];
              processors = [ "batch" ];
              exporters = [ "prometheus" ];
            };
          };
        };
      };
    };

    prometheus = {
      enable = true;
      scrapeConfigs = [
        {
          job_name = "otel-collector";
          static_configs = [
            { targets = [ "localhost:9464" ]; }
          ];
        }
      ];
    };
  };

  # https://devenv.sh/processes/
  # processes.dev.exec = "${lib.getExe pkgs.watchexec} -n -- ls -la";

  # https://devenv.sh/services/
  # services.postgres.enable = true;

}
