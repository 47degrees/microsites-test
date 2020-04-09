lazy val root = (project in file("."))
  .settings(moduleName := "microsites-root")
  .aggregate(microsites)
  .dependsOn(microsites)
  .settings(noPublishSettings: _*)

lazy val microsites =
  (project in file("microsites"))
    .settings(moduleName := "microsites")
    .enablePlugins(BuildInfoPlugin)
    .settings(
      buildInfoKeys := Seq[BuildInfoKey](
        name,
        version,
        "token" -> sys.env.getOrElse("GITHUB_TOKEN", "")
      ),
      buildInfoPackage := "microsites"
    )
    .settings(coreDeps: _*)

//////////
// DOCS //
//////////

lazy val docs = (project in file("docs"))
  .aggregate(microsites)
  .dependsOn(microsites)
  .settings(moduleName := "microsites-docs")
  .settings(micrositeSettings: _*)
  .settings(noPublishSettings: _*)
  .enablePlugins(MicrositesPlugin)

addCommandAlias("ci-test", "+scalafmtCheck; +scalafmtSbtCheck; +docs/mdoc; +test")
addCommandAlias("ci-docs", "docs/mdoc; headerCreateAll")
