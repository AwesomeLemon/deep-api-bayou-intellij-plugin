package com.github.awesomelemon;

import com.github.awesomelemon.deepapi.DeepApiDownloadProgressIndicatorWrapper;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;

public class ModelProvider {
    private final String tmpDir = System.getProperty("java.io.tmpdir");
    private final Path pluginTmpDir = Paths.get(tmpDir, "deep_api_plugin");
    private final String modelUrl = "https://www.dropbox.com/s/g8nc7v033xypbgv/deep-api-model.zip?dl=1";
    final String zipName = "model.zip";
//    private final String beamOpsLinux = "_beam_search_ops_linux.so";
//    private final String beamOpsMacos = "_beam_search_ops_macos.so";
//    private final String beamOpsWindows = "_beam_search_ops.dll";
    private final String exportedModelDir = "deep-api-model";

    public ModelProvider(DeepApiDownloadProgressIndicatorWrapper progress) {
        try {
//            OsCheck.OSType osType = OsCheck.getOperatingSystemType();
//            if (!(osType.equals(OsCheck.OSType.Linux) || osType.equals(OsCheck.OSType.Windows)
//                    || osType.equals(OsCheck.OSType.MacOS))) {
//                throw new RuntimeException("Only Windows, Linux, MacOS are supported!");
//            }
            if (!Files.exists(pluginTmpDir)) {
                pluginTmpDir.toFile().mkdir();
                Path path = download(new URL(modelUrl), Paths.get(pluginTmpDir.toString(), zipName), progress);
                progress.setText("Extracting archive");
                extractSubDir(path, pluginTmpDir);
            } else {
                Path modelDir = pluginTmpDir.resolve(exportedModelDir);

//                String beamOps = null;
//                if (osType.equals(OsCheck.OSType.Linux)) beamOps = beamOpsLinux;
//                if (osType.equals(OsCheck.OSType.Windows)) beamOps = beamOpsWindows;
//                Path beamOpPath = pluginTmpDir.resolve(beamOps);

                if (!Files.exists(modelDir) /*|| !Files.exists(beamOpPath)*/) {
                    Path zipPath = pluginTmpDir.resolve(zipName);
                    if (!Files.exists(zipPath)) {
                        zipPath = download(new URL(modelUrl), Paths.get(pluginTmpDir.toString(), zipName), progress);
                    }
                    extractSubDir(zipPath, pluginTmpDir);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void extractSubDir(Path zipFilePath, Path targetDir) throws IOException {
        FileSystem zipFs = FileSystems.newFileSystem(zipFilePath, null);
        Path pathInZip = zipFs.getPath("/");
        Files.walkFileTree(pathInZip, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path filePath, BasicFileAttributes attrs) throws IOException {
                // Make sure that we conserve the hierachy of files and folders inside the zip
                Path relativePathInZip = pathInZip.relativize(filePath);
                Path targetPath = targetDir.resolve(relativePathInZip.toString());
                Files.createDirectories(targetPath.getParent());

                // And extract the file
                Files.copy(filePath, targetPath, StandardCopyOption.REPLACE_EXISTING);

                return FileVisitResult.CONTINUE;
            }
        });
    }

//    public String getBeamOpsPath() {
//        String beamOps = null;
//        if (OsCheck.getOperatingSystemType().equals(OsCheck.OSType.Linux)) beamOps = beamOpsLinux;
//        else beamOps = beamOpsWindows;
//        return Paths.load(pluginTmpDir.toString(), beamOps).toString();
//    }

    public String getExportedModelPath() {
        return Paths.get(pluginTmpDir.toString(), exportedModelDir).toString();
    }

    private Path download(URL url, Path path, DeepApiDownloadProgressIndicatorWrapper progress) throws IOException {
        path.toFile().getParentFile().mkdirs();

        URLConnection conn = url.openConnection();
        int contentLength = conn.getContentLength();

        InputStream in = url.openStream();
        try (BufferedInputStream bis = new BufferedInputStream(in)) {
            FileOutputStream out = new FileOutputStream(path.toFile());
            byte[] data = new byte[1024];
            int totalCount = 0;
            int count = bis.read(data, 0, 1024);
            while (count != -1) {
                out.write(data, 0, count);
                totalCount += count;
                if (contentLength == 0) {
                    progress.setFraction(0.0);
                } else {
                    progress.setFraction(totalCount / (double) contentLength);
                }
                count = bis.read(data, 0, 1024);
            }
        }

        return path;
    }

    private final static class OsCheck {
        public enum OSType {
            Windows, MacOS, Linux, Other
        };

        static OSType detectedOS;

        public static OSType getOperatingSystemType() {
            if (detectedOS == null) {
                String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
                if ((OS.contains("mac")) || (OS.contains("darwin"))) {
                    detectedOS = OSType.MacOS;
                } else if (OS.contains("win")) {
                    detectedOS = OSType.Windows;
                } else if (OS.contains("nux")) {
                    detectedOS = OSType.Linux;
                } else {
                    detectedOS = OSType.Other;
                }
            }
            return detectedOS;
        }
    }

//    public static void main(String[] args) throws IOException {
//        ModelProvider modelProvider = new ModelProvider();
//    }
}
