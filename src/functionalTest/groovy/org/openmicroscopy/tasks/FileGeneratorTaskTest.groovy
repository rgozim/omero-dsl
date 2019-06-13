package org.openmicroscopy.tasks

import org.openmicroscopy.AbstractBaseTest

class FileGeneratorTaskTest extends AbstractBaseTest {

    def "can create task"() {
        buildFile << """
            task genSingleFile(type: )
        
        
        """
    }


}
