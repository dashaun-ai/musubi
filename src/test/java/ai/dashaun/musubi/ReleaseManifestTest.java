package ai.dashaun.musubi;

import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ReleaseManifestTest {

	@Test
	void cloudFoundryManifestRequiresBrokerAndBucketSecrets() throws Exception {
		String manifest = Files.readString(Path.of("manifest.yml"));
		assertThat(manifest).doesNotContain("dev-secret");
		assertThat(manifest).contains(
				"MUSUBI_BROKER_PASSWORD: ((musubi_broker_password))",
				"MUSUBI_GARAGE_RUN_ARTIFACTS_ACCESS_KEY: ((garage_run_artifacts_access_key))",
				"MUSUBI_GARAGE_RUN_ARTIFACTS_SECRET_KEY: ((garage_run_artifacts_secret_key))",
				"MUSUBI_GARAGE_BROWSER_EVIDENCE_ACCESS_KEY: ((garage_browser_evidence_access_key))",
				"MUSUBI_GARAGE_BROWSER_EVIDENCE_SECRET_KEY: ((garage_browser_evidence_secret_key))",
				"MUSUBI_GARAGE_BACKUPS_ACCESS_KEY: ((garage_backups_access_key))",
				"MUSUBI_GARAGE_BACKUPS_SECRET_KEY: ((garage_backups_secret_key))");
	}
}
