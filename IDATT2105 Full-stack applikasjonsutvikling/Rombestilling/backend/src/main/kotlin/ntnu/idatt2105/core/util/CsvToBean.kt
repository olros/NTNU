package ntnu.idatt2105.core.util

import com.opencsv.bean.CsvToBean
import com.opencsv.bean.CsvToBeanBuilder
import org.springframework.web.multipart.MultipartFile
import java.io.BufferedReader
import java.io.IOException

class CsvToBean {
    companion object {
        @JvmStatic
        public fun <T> createCSVToBean(fileReader: BufferedReader?, beanClass: Class<T>): CsvToBean<T> =
            CsvToBeanBuilder<T>(fileReader)
                .withType(beanClass)
                .withIgnoreLeadingWhiteSpace(true)
                .build()

        @JvmStatic
        public fun closeFileReader(fileReader: BufferedReader?) {
            try {
                fileReader!!.close()
            } catch (ex: IOException) {
                throw RuntimeException("Error during csv import")
            }
        }

        @JvmStatic
        public fun throwIfFileEmpty(file: MultipartFile) {
            if (file.isEmpty) {
                throw RuntimeException("Empty file")
            }
        }
    }
}
