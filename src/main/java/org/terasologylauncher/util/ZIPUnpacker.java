/*
 * Copyright (c) 2013 MovingBlocks
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.terasologylauncher.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author MrBarsack
 * @author Skaldarnar
 */
public class ZIPUnpacker {

    public static void extractArchive(File archive) throws IOException {
        extractArchive(archive, archive.getParentFile());
    }

    public static void extractArchive(File archive, File destDir) throws IOException {
        if (archive == null) {
            throw new IllegalArgumentException("No null allowed");
        }
        ZipFile zipFile = new ZipFile(archive);
        Enumeration entries = zipFile.entries();

        byte[] buffer = new byte[ 8192 ];
        int length;

        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String entryFileName = entry.getName();
            File dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            if (!entry.isDirectory()) {
                BufferedOutputStream bos = new BufferedOutputStream(
                    new FileOutputStream(new File(destDir, entryFileName)));

                BufferedInputStream bis = new BufferedInputStream(zipFile
                    .getInputStream(entry));

                while ((length = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, length);
                }

                bos.flush();
                bos.close();
                bis.close();
            }
        }
        zipFile.close();
    }

    private static File buildDirectoryHierarchyFor(String entryName, File destDir) {
        int lastIndex = entryName.lastIndexOf('/');
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }
}