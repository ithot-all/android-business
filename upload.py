from string import Template
import os

template = Template("gradlew clean build bintrayUpload -PbintrayUser=${user} -PbintrayKey=${key} -PdryRun=false -x javadocRelease")

os.system(template.substitute(user=os.getenv('BINTRAY_USER'),key=os.getenv('BINTRAY_KEY')))

print 'uploaded to bintray'