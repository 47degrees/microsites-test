import sbt.Resolver.sonatypeRepo

resolvers ++= Seq(sonatypeRepo("snapshots"), sonatypeRepo("releases"))

addSbtPlugin("com.eed3si9n"              % "sbt-buildinfo"     % "0.9.0")
addSbtPlugin("com.geirsson"              % "sbt-ci-release"    % "1.5.2")
addSbtPlugin("com.47deg"                 % "sbt-microsites"    % "1.1.4+22-e40d18a2-SNAPSHOT")
addSbtPlugin("org.scoverage"             % "sbt-scoverage"     % "1.6.1")
addSbtPlugin("org.scalameta"             % "sbt-scalafmt"      % "2.3.2")
addSbtPlugin("org.scalameta"             % "sbt-mdoc"          % "2.1.5")
addSbtPlugin("de.heikoseeberger"         % "sbt-header"        % "5.4.0")
addSbtPlugin("com.alejandrohdezma"       %% "sbt-github"       % "0.6.0")
addSbtPlugin("com.alejandrohdezma"       % "sbt-github-header" % "0.6.0")
addSbtPlugin("com.alejandrohdezma"       % "sbt-github-mdoc"   % "0.6.0")
addSbtPlugin("com.alejandrohdezma"       % "sbt-mdoc-toc"      % "0.2")
addSbtPlugin("io.github.davidgregory084" % "sbt-tpolecat"      % "0.1.11")
