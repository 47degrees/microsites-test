import com.typesafe.sbt.site.SitePlugin.autoImport._
import sbt.Keys._
import sbt._
import scoverage.ScoverageKeys._
import com.alejandrohdezma.sbt.github.SbtGithubPlugin
import mdoc.MdocPlugin.autoImport._
import microsites.ExtraMdFileConfig
import microsites.MicrositesPlugin.autoImport._

object ProjectPlugin extends AutoPlugin {

  override def trigger: PluginTrigger = allRequirements

  override def requires: Plugins = SbtGithubPlugin

  object autoImport {

    lazy val V = new {
      val github4s: String   = "0.23.0"
      val scala212: String   = "2.12.10"
      val scala213: String   = "2.13.1"
      val base64: String     = "0.2.9"
      val cats: String       = "2.1.1"
      val catsEffect: String = "2.1.1"
      val circe: String      = "0.13.0"
      val http4s: String     = "0.21.3"
      val paradise: String   = "2.1.1"
      val scalamock: String  = "4.4.0"
      val scalatest: String  = "3.1.1"
      val silencer: String   = "1.6.0"
    }

    lazy val noPublishSettings = Seq(
      publish := ((): Unit),
      publishLocal := ((): Unit),
      publishArtifact := false,
      publishMavenStyle := false // suppress warnings about intransitive deps (not published anyway)
    )

    lazy val micrositeSettings = Seq(
      micrositeName := "Microsites",
      micrositeDescription := "Github API wrapper written in Scala",
      micrositeBaseUrl := "microsites-tests",
      micrositeDocumentationUrl := "docs",
      micrositeGithubOwner := "47degrees",
      micrositeGithubRepo := "microsites-test",
      micrositeAuthor := "Microsites contributors",
      micrositeCompilingDocsTool := WithMdoc,
      micrositePushSiteWith := GitHub4s,
      micrositeGithubToken := sys.env.get("GITHUB_TOKEN"),
      micrositeOrganizationHomepage := "https://github.com/47degrees/microsites/blob/master/AUTHORS.md",
      micrositePalette := Map(
        "brand-primary"   -> "#3D3832",
        "brand-secondary" -> "#f90",
        "white-color"     -> "#FFFFFF"
      ),
      micrositeExtraMdFiles := Map(
        file("CHANGELOG.md") -> ExtraMdFileConfig(
          "changelog.md",
          "page",
          Map(
            "title"     -> "Changelog",
            "section"   -> "home",
            "position"  -> "3",
            "permalink" -> "changelog"
          )
        )
      ),
      micrositeExtraMdFilesOutput := mdocIn.value,
      includeFilter in makeSite := "*.html" | "*.css" | "*.png" | "*.jpg" | "*.gif" | "*.js" | "*.swf" | "*.md" | "*.svg",
      scalacOptions ~= (_ filterNot Set("-Ywarn-unused-import", "-Xlint", "-Xfatal-warnings").contains)
    )

    lazy val coreDeps = Seq(
      libraryDependencies ++= Seq(
        "com.47deg"             %% "github4s"            % V.github4s,
        "org.typelevel"         %% "cats-core"           % V.cats,
        "org.typelevel"         %% "cats-core"           % V.cats,
        "io.circe"              %% "circe-core"          % V.circe,
        "io.circe"              %% "circe-generic"       % V.circe,
        "io.circe"              %% "circe-literal"       % V.circe,
        "com.github.marklister" %% "base64"              % V.base64,
        "org.http4s"            %% "http4s-blaze-client" % V.http4s,
        "org.http4s"            %% "http4s-circe"        % V.http4s,
        "io.circe"              %% "circe-parser"        % V.circe % Test,
        "org.scalamock"         %% "scalamock"           % V.scalamock % Test,
        "org.scalatest"         %% "scalatest"           % V.scalatest % Test,
        "com.github.ghik"       % "silencer-lib"         % V.silencer % Provided cross CrossVersion.full,
        compilerPlugin("com.github.ghik" % "silencer-plugin" % V.silencer cross CrossVersion.full)
      ),
      libraryDependencies ++= (CrossVersion.partialVersion(scalaBinaryVersion.value) match {
        case Some((2, 13)) => Seq.empty[ModuleID]
        case _ =>
          Seq(compilerPlugin("org.scalamacros" %% "paradise" % V.paradise cross CrossVersion.full))
      })
    )

    def toCompileTestList(sequence: Seq[ProjectReference]): List[String] = sequence.toList.map {
      p =>
        val project: String = p.asInstanceOf[LocalProject].project
        s"$project/test"
    }
  }

  import autoImport.V

  override def projectSettings: Seq[Def.Setting[_]] =
    Seq(
      name := "microsites",
      organization := "com.47deg",
      organizationName := "47 Degrees",
      organizationHomepage := Some(url("https://www.47deg.com")),
      homepage := Option(url("https://47degrees.github.io/microsites/")),
      description := "Github API wrapper written in Scala",
      startYear := Option(2016),
      resolvers += Resolver.sonatypeRepo("snapshots"),
      crossScalaVersions := Seq(V.scala212, V.scala213),
      scalacOptions := {
        val withStripedLinter = scalacOptions.value filterNot Set("-Xlint", "-Xfuture").contains
        (CrossVersion.partialVersion(scalaBinaryVersion.value) match {
          case Some((2, 13)) => withStripedLinter :+ "-Ymacro-annotations"
          case _             => withStripedLinter
        }) :+ "-language:higherKinds"
      },
      coverageMinimum := 70d,
      coverageFailOnMinimum := true,
      coverageExcludedPackages := "<empty>;microsites\\.scalaz\\..*",
      // This is necessary to prevent packaging the BuildInfo with
      // sensible information like the Github token. Do not remove.
      mappings in (Compile, packageBin) ~= { (ms: Seq[(File, String)]) =>
        ms filter {
          case (_, toPath) =>
            !toPath.startsWith("microsites/BuildInfo")
        }
      }
    )
}
