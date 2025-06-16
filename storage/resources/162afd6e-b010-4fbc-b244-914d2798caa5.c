#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>
#include <fcntl.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <dirent.h>
#include <pwd.h>
#include <grp.h>
#include <errno.h>

// 缓冲区大小
#define BUF_SIZE 4096

// 复制文件
void copy_file(const char *src_path,const char *dest_path, struct stat *st){
    int src_fd = open(src_path, O_RDONLY);
    if(src_fd == -1){
        perror("open source file error");
        return;
    }

    int dest_fd = open(dest_path, O_WRONLY|O_CREAT|O_TRUNC, st->st_mode);
    if(dest_fd == -1){
        perror("open destination file error");
        close(src_fd);
        return;
    }

    char buf[BUF_SIZE];
    ssize_t bytes_read,bytes_written;

    while((bytes_read = read(src_fd,buf,BUF_SIZE))>0){
        bytes_written = write(dest_fd,buf,bytes_read);
        if(bytes_written != bytes_read){
            perror("write error");
            break;
        }
    }

    // 设置文件权限和属主
    fchmod(dest_fd,st->st_mode);
    fchown(dest_fd,st->st_uid,st->st_gid);

    close(src_fd);
    close(dest_fd);

    printf("Copied file complete: %s -> %s\n",src_path,dest_path);
}

// 递归复制目录
void copy_dir(const char *src_path,const char *dest_path){
    DIR *dir = opendir(src_path);
    if(!dir){
        perror("opendir failed");
        return;
    }

    struct dirent *entry;
    while((entry = readdir(dir)) != NULL){
        // 跳过当前目录和上级目录
        if(strcmp(entry->d_name,".")==0 ||strcmp(entry->d_name,"..")==0){
            continue;
        }

        char src_full[PATH_MAX],dest_full[PATH_MAX];
        snprintf(src_full,sizeof(src_full),"/%s/%s",src_path,entry->d_name);
        snprintf(dest_full,sizeof(dest_full),"/%s/%s",dest_path,entry->d_name);

        struct stat st;
        if(lstat(src_full,&st) == -1){
            perror("lstat error");
            continue;
        }

        // 如果是符号链接，跳过链接并提示
        if(S_ISLNK(st.st_mode)){
            printf("Skip link:%s\n",src_full);
            continue;
        }


        // 如果是目录，递归创建并复制
        if(S_ISDIR(st.st_mode)){
            if(mkdir(dest_full,st.st_mode) == -1 && errno != EEXIST){
                perror("mkdir error");
            }else{
                printf("Create dirctory:%s\n",dest_full);
                // 设置文件权限和属主
                chmod(dest_full,st.st_mode);
                chown(dest_full,st.st_uid,st.st_gid);
            }
            copy_dir(src_full,dest_full);
        }
        // 普通文件，调用copy_file
        else if(S_ISREG(st.st_mode)){
            copy_file(src_full,dest_full,&st);            
        }

        // 循环处理目录内所有文件
    }
    closedir(dir);
}

int main(int argc,char *argv[]){
    const char *src_dir = argv[1];
    const char *dest_dir = argv[2];

    struct stat st;
    if (stat(src_dir, &st) == -1) {
        perror("Source directory not found");
        exit(EXIT_FAILURE);
    }

    // 目标目录不存在则创建
    if (mkdir(dest_dir, st.st_mode) == -1) {
        if (errno != EEXIST) {
            perror("Target mkdir error");
            exit(EXIT_FAILURE);
        }
    } else {
        printf("Created target directory: %s\n", dest_dir);
    }

    // 设置目标目录的权限和属主
    chown(dest_dir, st.st_uid, st.st_gid);
    chmod(dest_dir, st.st_mode);

    // 开始复制
    copy_dir(src_dir, dest_dir);

    printf("Directory copy completed.\n");

    return 0;
}
