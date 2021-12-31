package jezorko.github.gfngameslist.versions

import kotlinx.serialization.Serializable

@Serializable
data class VersionInfo(val repositoryUrl: String, val commitSlug: String)